package cn.t.metric.common.reader;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.util.ChannelUtil;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelByteBufferReader implements EventReader {
    @Override
    public Object read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ChannelContext ctx = ChannelUtil.getChannelContext(key);

        return socketChannel;
    }
}
