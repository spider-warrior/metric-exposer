package cn.t.metric.common.bootstrap;

import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ServerBootstrap {
    public static void bind(String bindAddress, int bindPrt, ChannelInitializer initializer, SingleThreadEventLoop acceptLoop, SingleThreadEventLoop workerLoop) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定&监听
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(bindAddress, bindPrt), 128);
        // 注册accept事件
        acceptLoop.register(serverSocketChannel, SelectionKey.OP_ACCEPT, initializer).addListener(future -> {
            if(future.isSuccess()) {
                // 监听就绪
                future.get().getPipeline().invokeChannelReady(future.get());
            }
        });
        // 启动accept线程
        new Thread(acceptLoop, "accept-thread").start();
        // 启动worker线程
        new Thread(workerLoop, "worker-thread").start();
    }
}
