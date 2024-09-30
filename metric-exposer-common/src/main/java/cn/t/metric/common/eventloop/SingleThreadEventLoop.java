package cn.t.metric.common.eventloop;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.Promise;
import cn.t.metric.common.channel.UnPooledHeapByteBuf;
import cn.t.metric.common.constants.EventLoopStatus;
import cn.t.metric.common.exception.UnExpectedException;
import cn.t.metric.common.initializer.ChannelInitializer;
import cn.t.metric.common.pipeline.ChannelPipeline;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

public class SingleThreadEventLoop implements Runnable, Closeable {

    private static final long defaultNextSelectTimeout = 3000;

    private final PriorityBlockingQueue<EventLoopTask> taskQueue = new PriorityBlockingQueue<>();
    private final UnPooledHeapByteBuf byteBuf = new UnPooledHeapByteBuf();
    private volatile EventLoopStatus status = EventLoopStatus.NOT_STARTED;
    private final Selector selector;
    private volatile Thread thread;
    private long nextSelectTimeout = defaultNextSelectTimeout;
    private volatile long nextTaskExecuteTime;

    @Override
    public void run() {
        thread = Thread.currentThread();
        status = EventLoopStatus.STARTED;
        try {
            while (status == EventLoopStatus.STARTED) {
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
                        EventLoopTask task = taskQueue.peek();
                        if(task != null) {
                            //允许5毫秒抖动
                            if(task.getDeadlineMills() <= now + 5) {
                                taskQueue.remove().getCommand().run();
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
            this.status = EventLoopStatus.SHUTDOWN;
        }
    }

    @Override
    public void close() {
        this.status = EventLoopStatus.SHUTTING_DOWN;
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public Promise<ChannelContext> register(SelectableChannel channel, int ops, ChannelInitializer initializer) {
        if(inEventLoop(Thread.currentThread())) {
            Promise<ChannelContext> promise = new Promise<>();
            doRegister(channel, ops, initializer, promise);
            return promise;
        } else {
            Promise<ChannelContext> promise = new Promise<>();
            addTask(() -> doRegister(channel, ops, initializer, promise), 0);
            return promise;
        }
    }

    private void doRegister(SelectableChannel channel, int ops, ChannelInitializer initializer, Promise<ChannelContext> promise) {
        try {
            SelectionKey selectionKey = channel.register(this.selector, ops);
            ChannelPipeline pipeline = new ChannelPipeline();
            ChannelContext ctx = new ChannelContext(channel, selectionKey, pipeline);
            initializer.initChannel(ctx, channel);
            selectionKey.attach(ctx);
            promise.success(ctx);
        } catch (Throwable t) {
            promise.failure(t);
        }
    }

    public void addTask(Runnable runnable, int delayMills) {
        long runTime = System.currentTimeMillis() + delayMills;
        this.taskQueue.add(new EventLoopTask(runnable, runTime));
        //立即执行任务或任务执行时间早于下次任务执行时间, 此时可能任务正在执行中，暂时没考虑该情况
        if(runTime < nextTaskExecuteTime) {
            //todo 多线程会调用多次wakeup，且当selector未阻塞在select方法时被调用wakeup方法，那么当selector下次调用select方法时会直接返回。selectNow()可以清理掉之前任何调用wakeup带来的影响。
            this.selector.wakeup();
        }
    }

    public SingleThreadEventLoop() throws IOException {
        this.selector = Selector.open();
    }

}
