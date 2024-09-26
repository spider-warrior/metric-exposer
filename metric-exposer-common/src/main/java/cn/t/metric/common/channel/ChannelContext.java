package cn.t.metric.common.channel;

import cn.t.metric.common.pipeline.ChannelPipeline;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class ChannelContext<C extends NetworkChannel> {

    private final Map<String, Object> attrs = new HashMap<>();
    private final ChannelPipeline<C> channelPipeline = new ChannelPipeline<>(this);;
    private final C channel;
    private SelectionKey selectionKey;
    private ByteBuffer byteBuffer;

    public C getChannel() {
        return channel;
    }

    public void close() {
        if(this.channel.isOpen()) {
            try { this.channel.close(); } catch (IOException ignore) {}
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

    public ChannelPipeline<C> getChannelPipeline() {
        return channelPipeline;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public ChannelContext(C channel) {
        this.channel = channel;
    }

}
