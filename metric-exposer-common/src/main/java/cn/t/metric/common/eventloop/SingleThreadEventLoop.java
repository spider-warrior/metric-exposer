package cn.t.metric.common.eventloop;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.Promise;
import cn.t.metric.common.channel.UnPooledHeapByteBuf;
import cn.t.metric.common.constants.EventLoopState;
import cn.t.metric.common.exception.UnExpectedException;
import cn.t.metric.common.initializer.ChannelInitializer;
import cn.t.metric.common.pipeline.ChannelPipeline;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class SingleThreadEventLoop implements Runnable, Closeable {

    private static final AtomicIntegerFieldUpdater<SingleThreadEventLoop> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventLoop.class, "state");

    private static final boolean AWAKE = false;
    private static final long defaultNextSelectTimeout = 3000;

    private final PriorityBlockingQueue<EventLoopTask<?>> taskQueue = new PriorityBlockingQueue<>(10, Comparator.comparingLong(EventLoopTask::getDeadlineMills));
    private final UnPooledHeapByteBuf byteBuf = new UnPooledHeapByteBuf();

    private final String name;
    private final Selector selector;
    private volatile Thread thread;
    private volatile int state = EventLoopState.NOT_STARTED;
    private long nextSelectTimeout = defaultNextSelectTimeout;
    private volatile long nextTaskExecuteTime = Long.MAX_VALUE;
    private final AtomicBoolean taskAwakeUp = new AtomicBoolean(AWAKE);

    @Override
    public void run() {
        thread = Thread.currentThread();
        try {
            while (state == EventLoopState.STARTED) {
                int count = selector.select(nextSelectTimeout);
                if(count > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if(key.isValid()) {
                            if (key.isConnectable()) {
                                ChannelUtil.getChannelContext(key).invokeChannelReady();
                            }
                            if(key.isWritable()) {
                                //todo 连接可写
                            }
                            if(key.isAcceptable()) {
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                                ChannelUtil.getChannelContext(key).invokeChannelRead(serverSocketChannel.accept());
                            }
                            if(key.isReadable()) {
                                SocketChannel socketChannel = (SocketChannel)key.channel();
                                ChannelContext ctx = ChannelUtil.getChannelContext(key);
                                ByteBuffer buffer = ctx.getReadBuffer();
                                int lastReadLength = 0;
                                for (int i = 0; i < 5; i++) {
                                    lastReadLength = socketChannel.read(buffer);
                                    if (lastReadLength > 0) {
                                        buffer.flip();
                                        byteBuf.writeBytes(buffer);
                                        buffer.clear();
                                        if(lastReadLength < buffer.capacity()) {
                                            //消息已读完
                                            break;
                                        }
                                    } else {
                                        //消息已读完或连接已断开
                                        break;
                                    }
                                }
                                if(lastReadLength > -1) {
                                    ctx.invokeChannelRead(byteBuf);
                                } else {
                                    //连接已关闭
                                    ctx.invokeChannelClose();
                                    //取消注册
                                    key.cancel();
                                }
                            }
                        } else {
                            throw new UnExpectedException("loop key is invalid");
                        }
                    }
                }
                if(!taskQueue.isEmpty()) {
                    while (true) {
                        long now = System.currentTimeMillis();
                        EventLoopTask<?> task = taskQueue.peek();
                        if(task != null) {
                            //允许5毫秒抖动
                            if(task.getDeadlineMills() <= now + 5) {
                                taskQueue.remove().run();
                            } else {
                                //剩余任务需要下次执行，根据下个任务执行之间设置select策略
                                nextTaskExecuteTime = task.getDeadlineMills();
                                nextSelectTimeout = nextTaskExecuteTime - now;
                                break;
                            }
                        } else {
                            //任务执行完毕，执行默认select策略
                            nextTaskExecuteTime = now + defaultNextSelectTimeout;
                            nextSelectTimeout = defaultNextSelectTimeout;
                            break;
                        }
                    }
                } else {
                    //任务为空，执行默认select策略
                    nextTaskExecuteTime = System.currentTimeMillis() + defaultNextSelectTimeout;
                    nextSelectTimeout = defaultNextSelectTimeout;
                }
            }
        } catch (Throwable t) {
            System.out.println(ExceptionUtil.getErrorMessage(t));
        } finally {
            try { this.selector.close(); } catch (Throwable t) {System.out.println(ExceptionUtil.getErrorMessage(t));}
            this.state = EventLoopState.SHUTDOWN;
        }
    }

    @Override
    public void close() {
        this.state = EventLoopState.SHUTTING_DOWN;
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public Promise<ChannelContext> register(SelectableChannel channel, int ops, ChannelInitializer initializer) {
        Promise<ChannelContext> promise = new Promise<>();
        addTask(() -> doRegister(channel, ops, initializer), 0, promise);
        return promise;
    }

    private ChannelContext doRegister(SelectableChannel channel, int ops, ChannelInitializer initializer) throws Exception {
        SelectionKey selectionKey = channel.register(this.selector, ops);
        ChannelPipeline pipeline = new ChannelPipeline();
        ChannelContext ctx = new ChannelContext(channel, selectionKey, pipeline);
        initializer.initChannel(ctx, channel);
        selectionKey.attach(ctx);
        return ctx;
    }

    public <V> void addTask(Callable<V> callable, int delayMills, Promise<V> promise) {
        startThread();
        long runTime = System.currentTimeMillis() + delayMills;
        this.taskQueue.add(new EventLoopTask<>(callable, runTime, promise));
        //如果任务需要立即执行且selector未被唤醒
        if(runTime < nextTaskExecuteTime && taskAwakeUp.compareAndSet(false, true)) {
            //多线程会调用多次wakeup，且当selector未阻塞在select方法时被调用wakeup方法，那么当selector下次调用select方法时会直接返回。selectNow()可以清理掉之前任何调用wakeup带来的影响。
            this.selector.wakeup();
        }
    }

    private void startThread() {
        if(state == EventLoopState.NOT_STARTED) {
            //double check
            if(STATE_UPDATER.compareAndSet(this, EventLoopState.NOT_STARTED, EventLoopState.STARTED)) {
                state = EventLoopState.STARTED;
                new Thread(this, name).start();
            }
        }
    }

    public SingleThreadEventLoop(String name) throws IOException {
        this.name = name;
        this.selector = Selector.open();
    }

}
