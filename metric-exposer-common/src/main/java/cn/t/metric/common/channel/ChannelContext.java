package cn.t.metric.common.channel;

import cn.t.metric.common.pipeline.ChannelPipeline;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class ChannelContext {

    private final Map<String, Object> attrs = new HashMap<>();
    private final Channel channel;
    private final SelectionKey selectionKey;
    private final ChannelPipeline pipeline;
    private ByteBuffer readBuffer;

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        if(this.channel.isOpen()) {
            try { this.channel.close(); } catch (IOException ignore) {}
        }
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public void setReadBuffer(ByteBuffer readBuffer) {
        this.readBuffer = readBuffer;
    }

    public Object getAttribute(String name) {
        return this.attrs.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.attrs.put(name, value);
    }

    public ChannelPipeline getPipeline() {
        return pipeline;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void invokeChannelReady() {
        pipeline.invokeChannelReady(this);
    }

    public void invokeNextChannelReady() {
        pipeline.invokeNextChannelReady(this);
    }

    public void invokeChannelRead(Object msg) {
        pipeline.invokeChannelRead(this, msg);
    }

    public void invokeNextChannelRead(Object msg) {
        pipeline.invokeNextChannelRead(this, msg);
    }

    public void invokeChannelWrite(Object msg) {
        pipeline.invokeChannelWrite(this, msg);
    }

    public void invokeNextChannelWrite(Object msg) {
        pipeline.invokeNextChannelWrite(this, msg);
    }

    public void invokeChannelClose() {
        pipeline.invokeChannelClose(this);
    }

    public void invokeNextChannelClose() {
        pipeline.invokeNextChannelClose(this);
    }

    public void invokeChannelError(Throwable t) {
        pipeline.invokeChannelError(this, t);
    }

    public void invokeNextChannelError(Throwable t) {
        pipeline.invokeNextChannelError(this, t);
    }

    public ChannelContext(Channel channel, SelectionKey selectionKey, ChannelPipeline pipeline) {
        this.channel = channel;
        this.selectionKey = selectionKey;
        this.pipeline = pipeline;
    }
}
