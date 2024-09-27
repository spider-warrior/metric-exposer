package cn.t.metric.client;

import cn.t.metric.common.bootstrap.ClientBootstrap;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;

public class MetricExposerClientApplication {
    public static void main(String[] args) throws Exception {
        String serverHost = "127.0.0.1";
        int serverPort = 5000;
        ChannelInitializer channelInitializer = (ctx, ch) -> ctx.getPipeline().addChannelHandlerLast(new CmdRequestHandler());
        ClientBootstrap.connect(serverHost, serverPort, channelInitializer, new SingleThreadEventLoop());
    }
}
