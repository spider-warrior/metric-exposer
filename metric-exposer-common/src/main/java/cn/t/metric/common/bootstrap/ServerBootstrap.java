package cn.t.metric.common.bootstrap;

import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.ConnectionAcceptorHandler;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerBootstrap {
    public static ServerSocketChannel bind(String bindAddress, int bindPrt, ChannelInitializer<SocketChannel> channelInitializer, SingleThreadEventLoop acceptLoop, SingleThreadEventLoop workerLoop) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定&监听
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(bindAddress, bindPrt), 128);

        // 构建serverSocketChannelContext
        ChannelContext<ServerSocketChannel> serverSocketCtx = new ChannelContext<>(serverSocketChannel);
        serverSocketCtx.getChannelPipeline().addMessageHandlerFirst(new ConnectionAcceptorHandler(channelInitializer, workerLoop));

        // 监听就绪
        serverSocketCtx.getChannelPipeline().invokeChannelReady();

        // 注册accept事件
        acceptLoop.register(serverSocketChannel, SelectionKey.OP_ACCEPT, serverSocketCtx);

        // 启动worker线程
        new Thread(workerLoop, "worker-thread").start();
        // 启动accept线程
        new Thread(acceptLoop, "accept-thread").start();

        return serverSocketChannel;
    }
}
