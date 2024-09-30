package cn.t.metric.common.bootstrap;

import cn.t.metric.common.eventloop.SingleThreadEventLoop;
import cn.t.metric.common.exception.UnExpectedException;
import cn.t.metric.common.initializer.ChannelInitializer;
import cn.t.metric.common.initializer.ServerSocketChannelInitializer;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ServerBootstrap {
    public static void bind(String bindAddress, int bindPrt, ChannelInitializer socketChannelInitializer, SingleThreadEventLoop acceptLoop, SingleThreadEventLoop workerLoop) throws Exception {
        //bind port
        workerLoop.addTask(() -> doBind(bindAddress, bindPrt), 0).addListener(bindFuture -> {
            if (bindFuture.isSuccess()) {
                //register accept event
                acceptLoop.addTask(() -> acceptLoop.register(bindFuture.get(), SelectionKey.OP_ACCEPT, new ServerSocketChannelInitializer(socketChannelInitializer, workerLoop)), 0).addListener(regFuture -> {
                    // 监听就绪
                    regFuture.get().invokeChannelReady();
                });
            } else {
                throw new UnExpectedException("register accept failed");
            }
        });
    }

    public static ServerSocketChannel doBind(String bindAddress, int bindPrt) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定&监听
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(bindAddress, bindPrt), 128);
        return serverSocketChannel;
    }

}
