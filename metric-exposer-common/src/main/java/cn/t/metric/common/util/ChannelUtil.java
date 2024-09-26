
package cn.t.metric.common.util;


import cn.t.metric.common.channel.ChannelContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class ChannelUtil {

    public static ByteBuffer getChannelBuffer(SelectionKey key) {
        ChannelContext<? extends NetworkChannel> channelContext = ChannelUtil.getChannelContext(key);
        ByteBuffer readBuffer = channelContext.getByteBuffer();
        if(readBuffer == null) {
            readBuffer = ByteBuffer.allocate(4096);
            channelContext.setByteBuffer(readBuffer);
        }
        return readBuffer;
    }

    public static void closeChannel(SelectionKey key) {
        key.cancel();
        Optional.ofNullable(getChannelContext(key)).ifPresent(ChannelContext::close);
    }

    public static ChannelContext<? extends NetworkChannel> getChannelContext(SelectionKey key) {
        return (ChannelContext<? extends NetworkChannel>)key.attachment();
    }

    public static void write(SocketChannel channel, byte[] bytes) throws IOException {
        if (bytes != null && bytes.length > 0) {
            write(channel, ByteBuffer.allocate(bytes.length).put(bytes));
        }
    }
    public static synchronized void write(SocketChannel channel, ByteBuffer buffer) throws IOException {
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
