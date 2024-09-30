package cn.t.metric.common.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.eventloop.SingleThreadEventLoop;
import cn.t.metric.common.exception.UnExpectedException;
import cn.t.metric.common.initializer.ChannelInitializer;

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionAcceptorHandler implements ChannelHandler {

    private final ChannelInitializer channelInitializer;
    private final SingleThreadEventLoop workerLoop;

    @Override
    public void read(ChannelContext ctx, Object msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)msg;
        socketChannel.configureBlocking(false);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        //注册读事件
        workerLoop.addTask(() -> workerLoop.register(socketChannel, SelectionKey.OP_READ, channelInitializer), 0).addListener(future -> {
            if (future.isSuccess()) {
                ChannelContext subCtx = future.get();
                // 初始化缓冲池
                subCtx.setReadBuffer(ByteBuffer.allocate(1024 * 1024));
                // 连接就绪
                subCtx.invokeChannelReady();
            } else {
                throw new UnExpectedException("register read failed");
            }
        });
    }

    @Override
    public void ready(ChannelContext ctx) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)ctx.getChannel();
        System.out.println("[channel-ready]bind port success, channel: " + serverSocketChannel);
    }

    public ConnectionAcceptorHandler(ChannelInitializer channelInitializer, SingleThreadEventLoop workerLoop) {
        this.channelInitializer = channelInitializer;
        this.workerLoop = workerLoop;
    }
}
