package cn.t.metric.common.channel;

import cn.t.metric.common.constants.EventLoopStatus;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class SingleThreadEventLoop implements Runnable, Closeable {

    private volatile EventLoopStatus status = EventLoopStatus.NOT_STARTED;
    private final Selector selector;

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
                        try {
                            ChannelUtil.getChannelContext(key).invokeChannelRead(key);
                        } catch (Throwable t) {
                            ChannelUtil.getChannelContext(key).invokeChannelError(t);
                        }
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

    public final SelectionKey register(SelectableChannel selectableChannel, int ops, Object attr) throws ClosedChannelException {
        return selectableChannel.register(this.selector, ops, attr);
    }

    public SingleThreadEventLoop() throws IOException {
        this.selector = Selector.open();
    }
}
