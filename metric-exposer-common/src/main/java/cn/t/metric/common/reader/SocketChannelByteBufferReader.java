package cn.t.metric.common.reader;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.util.ChannelUtil;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelByteBufferReader implements EventReader {
    @Override
    public Object read(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ChannelContext ctx = ChannelUtil.getChannelContext(key);
        ByteBuffer buffer = ctx.getReadBuffer();
        socketChannel.read(buffer);
        return socketChannel;
    }
}
