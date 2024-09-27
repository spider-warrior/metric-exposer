package cn.t.metric.common.reader;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ServerSocketChannelAcceptReader implements EventReader {
    @Override
    public Object read(SelectionKey key) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        return serverSocketChannel.accept();
    }
}
