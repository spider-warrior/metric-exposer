package cn.t.metric.common.context;

import cn.t.metric.common.handler.ChannelHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.util.*;

public class ChannelContext<C extends NetworkChannel> {

    private final C channel;
    private final List<ChannelHandler<C>> channelHandlerList = new ArrayList<>();
    private final Map<String, Object> attrs = new HashMap<>();
    private Iterator<ChannelHandler<C>> channelReadyIt;
    private Iterator<ChannelHandler<C>> channelReadIt;
    private Iterator<ChannelHandler<C>> channelWriteIt;
    private Iterator<ChannelHandler<C>> channelCloseIt;
    private ByteBuffer byteBuffer;

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
        this.channelReadIt = channelHandlerList.iterator();
        this.invokeNextChannelRead(msg);
    }

    public void invokeNextChannelRead(Object msg) {
        try {
            this.channelReadIt.next().read(this, msg);
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
            this.channelWriteIt.next().write(this, msg);
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
            this.channelCloseIt.next().close(this);
        } catch (Throwable subThrowable) {
            invokeNextChannelError(subThrowable);
        }
    }

    public void invokeChannelError(Throwable t) {
        this.channelReadIt = channelHandlerList.iterator();
        this.invokeNextChannelError(t);
    }

    public void invokeNextChannelError(Throwable t) {
        try {
            this.channelReadIt.next().error(this, t);
        } catch (Throwable subThrowable) {
            invokeNextChannelError(subThrowable);
        }
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public Object getAttribute(String name) {
        return this.attrs.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.attrs.put(name, value);
    }

    public ChannelContext(C channel) {
        this.channel = channel;
    }
}
