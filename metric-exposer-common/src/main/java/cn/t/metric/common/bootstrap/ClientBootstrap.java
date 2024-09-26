package cn.t.metric.common.bootstrap;

import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.context.ChannelContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ClientBootstrap {
    public static SocketChannel connect(String serverHost, int serverPort, ChannelInitializer<SocketChannel> channelInitializer, SingleThreadEventLoop workerLoop) throws Exception {
        // 连接
        SocketChannel socketChannel = connect(serverHost, serverPort);

        // 构建channelContext
        ChannelContext<SocketChannel> channelContext = new ChannelContext<>(socketChannel);
        channelInitializer.initChannel(channelContext, socketChannel);

        // 连接就绪
        channelContext.invokeChannelReady();

        // 注册read事件
        workerLoop.register(socketChannel, SelectionKey.OP_READ, channelContext);

        // 启动worker线程
        new Thread(workerLoop).start();

        return socketChannel;
    }
    private static SocketChannel connect(String ip, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(ip, port));
        System.out.println(ip + ":" + port + ",连接中....");
        while (!socketChannel.finishConnect()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
        }
        System.out.printf("连接已完成!, timestamp: %d%n", System.currentTimeMillis());
        return socketChannel;
    }

}
