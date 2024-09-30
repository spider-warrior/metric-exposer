package cn.t.metric.common.bootstrap;

import cn.t.metric.common.initializer.ChannelInitializer;
import cn.t.metric.common.eventloop.SingleThreadEventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ClientBootstrap {
    public static void connect(String serverHost, int serverPort, ChannelInitializer initializer, SingleThreadEventLoop workerLoop) throws Exception {
        // 连接
        SocketChannel socketChannel = connect(serverHost, serverPort);
        // 注册read事件
        workerLoop.register(socketChannel, SelectionKey.OP_READ, initializer).addListener(future -> {
            if (future.isSuccess()) {
                // 连接就绪
                future.get().invokeChannelReady();
            }
        });
        // 启动worker线程
        new Thread(workerLoop).start();
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
