
package cn.t.metric.common.util;


import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.constants.ChannelAttrName;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;

public class ChannelUtil {

    public static ByteBuffer getChannelBuffer(SelectionKey key) {
        Map<String, Object> attachment = ChannelUtil.getAttachment(key);
        ByteBuffer readBuffer = (ByteBuffer)attachment.get(ChannelAttrName.attrAccumulateBuf);
        if(readBuffer == null) {
            readBuffer = ByteBuffer.allocate(4096);
            attachment.put(ChannelAttrName.attrAccumulateBuf, readBuffer);
        }
        return readBuffer;
    }

    public static void closeChannel(SelectionKey key) {
        key.cancel();
        Optional.ofNullable(getChannelContext(key)).ifPresent(ChannelContext::close);
    }

    public static ChannelContext getChannelContext(SelectionKey key) {
        return (ChannelContext)ChannelUtil.getAttachment(key).get(ChannelAttrName.attrChannelContext);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAttachment(SelectionKey key) {
        return (Map<String, Object>)key.attachment();
    }

    public static void write(SocketChannel channel, byte[] bytes) throws IOException {
        if (bytes != null && bytes.length > 0) {
            write(channel, ByteBuffer.allocate(bytes.length).put(bytes));
        }
    }
    public static synchronized void write(SocketChannel channel, ByteBuffer buffer) throws IOException {
        if (!channel.isOpen() || !channel.isConnected()) {
            throw new IOException("连接已关闭(open&connected)");
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
