package cn.t.metric.common.context;

import cn.t.metric.common.handler.ChannelHandler;

import java.io.IOException;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ChannelContext<C extends NetworkChannel> {

    private final C channel;
    private final List<ChannelHandler<C>> channelHandlerList = new ArrayList<>();
    private Iterator<ChannelHandler<C>> channelReadyIt;
    private Iterator<ChannelHandler<C>> messageReadIt;
    private Iterator<ChannelHandler<C>> messageWriteIt;

    public C getChannel() {
        return channel;
    }

    public void close() {
        if(this.channel.isOpen()) {
            try { this.channel.close(); } catch (IOException ignore) {}
        }
    }

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
            channelReadyIt.next().ready(this);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelRead(Object msg) {
        this.messageReadIt = channelHandlerList.iterator();
        this.invokeNextChannelRead(msg);
    }

    public void invokeNextChannelRead(Object msg) {
        try {
            messageReadIt.next().read(this, msg);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelWrite(Object msg) {
        this.messageWriteIt = channelHandlerList.iterator();
        this.invokeNextChannelWrite(msg);
    }

    public void invokeNextChannelWrite(Object msg) {
        try {
            messageWriteIt.next().write(this, msg);
        } catch (Throwable t) {
            invokeNextChannelError(t);
        }
    }

    public void invokeChannelError(Throwable t) {
        this.messageReadIt = channelHandlerList.iterator();
        this.invokeNextChannelError(t);
    }

    public void invokeNextChannelError(Throwable t) {
        try {
            messageReadIt.next().error(this, t);
        } catch (Throwable subThrowable) {
            invokeNextChannelError(subThrowable);
        }
    }

    public ChannelContext(C channel) {
        this.channel = channel;
    }
}
