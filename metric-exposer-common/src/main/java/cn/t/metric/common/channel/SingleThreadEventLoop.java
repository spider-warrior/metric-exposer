package cn.t.metric.common.channel;

import cn.t.metric.common.constants.EventLoopStatus;
import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.SelectorOpenException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class SingleThreadEventLoop implements Runnable {

    private volatile EventLoopStatus status = EventLoopStatus.NOT_STARTED;
    private volatile Throwable error;
    private final Selector selector;
    private final ChannelContext channelContext;

    @Override
    public void run() {
        status = EventLoopStatus.STARTED;
        try {
            while (status == EventLoopStatus.STARTED) {
                int count = selector.select(3000);
                if(count > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        channelContext.invokeHandlerRead(key);
                    }
                }
            }
        } catch (Throwable t) {
            error = t;
        } finally {
            release();
            status = EventLoopStatus.SHUTDOWN;
        }
    }

    public void shutdown() {
        status = EventLoopStatus.SHUTTING_DOWN;
    }

    public void release() {
        try { this.selector.close();} catch (IOException ignore) {}
    }

    public Throwable getError() {
        return error;
    }

    public SingleThreadEventLoop(ChannelContext channelContext) {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new SelectorOpenException(e);
        }
        this.channelContext = channelContext;
    }
}
