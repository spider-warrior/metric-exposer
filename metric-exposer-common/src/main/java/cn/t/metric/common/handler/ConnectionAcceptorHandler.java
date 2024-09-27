package cn.t.metric.common.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;

import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionAcceptorHandler implements ChannelHandler {

    private final ChannelInitializer channelInitializer;
    private final SingleThreadEventLoop workerLoop;

    @Override
    public void read(ChannelContext ctx, Object msg) throws Exception {
        SelectionKey selectionKey = (SelectionKey)msg;
        SocketChannel socketChannel = ((ServerSocketChannel)selectionKey.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        //注册读事件
        workerLoop.register(socketChannel, SelectionKey.OP_READ, channelInitializer).addListener(future -> {
            if (future.isSuccess()) {
                // 连接就绪
                future.get().getPipeline().invokeChannelReady(future.get());
            }
        });
    }

    @Override
    public void ready(ChannelContext ctx) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)ctx.getChannel();
        System.out.println("channel ready: " + serverSocketChannel);
    }

    public ConnectionAcceptorHandler(ChannelInitializer channelInitializer, SingleThreadEventLoop workerLoop) {
        this.channelInitializer = channelInitializer;
        this.workerLoop = workerLoop;
    }
}
