package cn.t.metric.server;

import cn.t.metric.common.bootstrap.ServerBootstrap;
import cn.t.metric.common.eventloop.SingleThreadEventLoop;
import cn.t.metric.server.channel.SocketChannelInitializer;

public class MetricExposerServerApplication {
    public static void main(String[] args) throws Exception {
        int bindPrt = 5000;
        String bindAddress = "127.0.0.1";
        ServerBootstrap.bind(bindAddress, bindPrt, new SocketChannelInitializer(), new SingleThreadEventLoop(), new SingleThreadEventLoop());
    }
}
