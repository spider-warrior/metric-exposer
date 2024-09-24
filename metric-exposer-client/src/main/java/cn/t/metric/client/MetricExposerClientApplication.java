package cn.t.metric.client;

import cn.t.metric.common.bootstrap.ClientBootstrap;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.context.ChannelContext;

import java.nio.channels.SocketChannel;

public class MetricExposerClientApplication {
    public static void main(String[] args) throws Exception {
        String serverHost = "127.0.0.1";
        int serverPort = 5000;
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(ChannelContext<SocketChannel> ctx, SocketChannel ch) throws Exception {
                ctx.addMessageHandlerLast(new CmdRequestHandler());
            }
        };
        ClientBootstrap.connect(serverHost, serverPort, channelInitializer, new SingleThreadEventLoop());
    }
}
