package cn.t.metric.common.reader;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelByteBufferReader implements EventReader{
    @Override
    public Object read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        return socketChannel;
    }
}
