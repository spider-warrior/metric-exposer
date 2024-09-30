package cn.t.metric.client;

import cn.t.metric.client.channel.SocketChannelInitializer;
import cn.t.metric.common.bootstrap.ClientBootstrap;
import cn.t.metric.common.eventloop.SingleThreadEventLoop;

public class MetricExposerClientApplication {
    public static void main(String[] args) throws Exception {
        String serverHost = "127.0.0.1";
        int serverPort = 5000;
        ClientBootstrap.connect(serverHost, serverPort, new SocketChannelInitializer(), new SingleThreadEventLoop());
    }
}
