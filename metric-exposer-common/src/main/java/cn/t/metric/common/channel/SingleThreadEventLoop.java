package cn.t.metric.common.channel;

import cn.t.metric.common.constants.EventLoopStatus;
import cn.t.metric.common.exception.UnExpectedException;
import cn.t.metric.common.pipeline.ChannelPipeline;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SingleThreadEventLoop implements Runnable, Closeable {

    private final BlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>();
    private final UnPooledHeapByteBuf byteBuf = new UnPooledHeapByteBuf();
    private volatile EventLoopStatus status = EventLoopStatus.NOT_STARTED;
    private final Selector selector;
    private volatile Thread thread;

    @Override
    public void run() {
        thread = Thread.currentThread();
        status = EventLoopStatus.STARTED;
        try {
            while (status == EventLoopStatus.STARTED) {
                int count = selector.select(3000);
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
                                ctx.invokeChannelRead(byteBuf);
                                if(lastReadLength < 0) {
                                    //连接已关闭
                                    ctx.invokeChannelClose();
                                }
                            }
                        } else {
                            throw new UnExpectedException("loop key is invalid");
                        }
                    }
                }
                while(!taskQueue.isEmpty()) {
                    Runnable runnable = taskQueue.poll();
                    if(runnable != null) {
                        runnable.run();
                    }
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
            addTask(() -> doRegister(channel, ops, initializer, promise));
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

    public void addTask(Runnable runnable) {
        this.taskQueue.add(runnable);
        //todo 多线程会调用多次wakeup，且当selector未阻塞在select方法时被调用wakeup方法，那么当selector下次调用select方法时会直接返回。selectNow()可以清理掉之前任何调用wakeup带来的影响。
        this.selector.wakeup();
    }

    public SingleThreadEventLoop() throws IOException {
        this.selector = Selector.open();
    }

}
