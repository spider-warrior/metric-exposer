package cn.t.metric.common.channel;

import cn.t.metric.common.constants.EventLoopStatus;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SingleThreadEventLoop implements Runnable, Closeable {

    private final BlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>();
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
                                ChannelUtil.getChannelContext(key).getChannelPipeline().invokeChannelReady();
                            }
                            if(key.isWritable()) {
                                //todo 连接可写
                            }
                            if(key.isReadable() || key.isAcceptable()) {
                                ChannelUtil.getChannelContext(key).getChannelPipeline().invokeChannelRead(key);
                            }
                        } else {
                            // 连接关闭
                            ChannelUtil.getChannelContext(key).getChannelPipeline().invokeChannelClose();
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

    public final void register(SelectableChannel selectableChannel, int ops, ChannelContext<?> channelContext) throws ClosedChannelException {
        if(inEventLoop(Thread.currentThread())) {
            selectableChannel.register(this.selector, ops, channelContext);
        } else {
            addTask(() -> {
                try {
                    selectableChannel.register(this.selector, ops, channelContext);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
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
