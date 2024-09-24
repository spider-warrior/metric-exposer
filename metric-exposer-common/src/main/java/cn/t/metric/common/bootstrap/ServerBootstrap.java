package cn.t.metric.common.bootstrap;

import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.constants.ChannelAttrName;
import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.BootstrapException;
import cn.t.metric.common.handler.ConnectionAcceptorHandler;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ServerBootstrap {
    public static void bind(String bindAddress, int bindPrt, ChannelInitializer<SocketChannel> channelInitializer, SingleThreadEventLoop acceptLoop, SingleThreadEventLoop workerLoop) {
        try (
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
        ) {
            // 绑定&监听
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(bindAddress, bindPrt), 128);

            // 构建serverSocketChannelContext
            ChannelContext<ServerSocketChannel> serverSocketChannelContext = new ChannelContext<>(serverSocketChannel);
            serverSocketChannelContext.addMessageHandlerFirst(new ConnectionAcceptorHandler(channelInitializer, workerLoop));

            // 监听就绪
            serverSocketChannelContext.invokeChannelReady();

            // 注册accept事件
            Map<String, Object> attrs = new HashMap<>();
            attrs.put(ChannelAttrName.attrChannelContext, serverSocketChannelContext);
            acceptLoop.register(serverSocketChannel, SelectionKey.OP_ACCEPT, attrs);

            // 启动worker线程
            new Thread(workerLoop).start();
            // 启动accept线程
            new Thread(acceptLoop).start();

            LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(5));
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
    }
}
