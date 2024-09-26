package cn.t.metric.common.channel;

import cn.t.metric.common.handler.ChannelHandler;

import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChannelPipeline<C extends NetworkChannel> {
    private final List<ChannelHandler<C>> channelHandlerList = new ArrayList<>();
    private final ChannelContext<C> ctx;

    private Iterator<ChannelHandler<C>> channelReadyIt;
    private Iterator<ChannelHandler<C>> channelReadIt;
    private Iterator<ChannelHandler<C>> channelWriteIt;
    private Iterator<ChannelHandler<C>> channelCloseIt;

    public void addMessageHandlerLast(ChannelHandler<C> channelHandler) {
        this.channelHandlerList.add(channelHandler);
    }

    public void addMessageHandlerFirst(ChannelHandler<C> channelHandler) {
        this.channelHandlerList.add(0, channelHandler);
    }

    public void invokeChannelReady() {
        this.channelReadyIt = channelHandlerList.iterator();
        this.invokeNextChannelReady();
    }

    public void invokeNextChannelReady() {
        try {
            channelReadyIt.next().ready(this.ctx);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelRead(Object msg) {
        this.channelReadIt = channelHandlerList.iterator();
        this.invokeNextChannelRead(msg);
    }

    public void invokeNextChannelRead(Object msg) {
        try {
            this.channelReadIt.next().read(this.ctx, msg);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelWrite(Object msg) {
        this.channelWriteIt = channelHandlerList.iterator();
        this.invokeNextChannelWrite(msg);
    }

    public void invokeNextChannelWrite(Object msg) {
        try {
            this.channelWriteIt.next().write(this.ctx, msg);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelClose() {
        this.channelCloseIt = channelHandlerList.iterator();
        this.invokeNextChannelClose();
    }

    public void invokeNextChannelClose() {
        try {
            this.channelCloseIt.next().close(this.ctx);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelError(Throwable t) {
        this.channelReadIt = channelHandlerList.iterator();
        this.invokeNextChannelError(t);
    }

    public void invokeNextChannelError(Throwable t) {
        try {
            this.channelReadIt.next().error(this.ctx, t);
        } catch (Throwable subThrowable) {
            invokeNextChannelError(t);
        }
    }

    public ChannelPipeline(ChannelContext<C> ctx) {
        this.ctx = ctx;
    }
}
