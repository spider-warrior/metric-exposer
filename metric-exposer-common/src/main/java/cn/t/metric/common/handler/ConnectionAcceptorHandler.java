package cn.t.metric.common.handler;

import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.constants.ChannelAttrName;
import cn.t.metric.common.context.ChannelContext;

import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ConnectionAcceptorHandler implements ChannelHandler<ServerSocketChannel> {

    private final ChannelInitializer<SocketChannel> channelInitializer;
    private final SingleThreadEventLoop workerLoop;

    @Override
    public void read(ChannelContext<ServerSocketChannel> ctx, Object msg) throws Exception {
        SelectionKey selectionKey = (SelectionKey)msg;
        SocketChannel socketChannel = ((ServerSocketChannel)selectionKey.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        ChannelContext<SocketChannel> channelContext = new ChannelContext<>(socketChannel);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ChannelAttrName.attrChannelContext, channelContext);
        //初始化channel
        channelInitializer.initChannel(channelContext, socketChannel);
        //注册读事件
        workerLoop.register(socketChannel, SelectionKey.OP_READ, attrs);
        // 连接就绪
        channelContext.invokeChannelReady();
    }

    @Override
    public void ready(ChannelContext<ServerSocketChannel> ctx) {
        ServerSocketChannel serverSocketChannel = ctx.getChannel();
        System.out.println("channel ready: " + serverSocketChannel);
    }

    public ConnectionAcceptorHandler(ChannelInitializer<SocketChannel> channelInitializer, SingleThreadEventLoop workerLoop) {
        this.channelInitializer = channelInitializer;
        this.workerLoop = workerLoop;
    }
}
