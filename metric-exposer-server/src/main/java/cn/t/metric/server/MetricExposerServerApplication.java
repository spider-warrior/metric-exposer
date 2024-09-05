package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContextManager;

public class MetricExposerServerApplication {
    public static void main(String[] args) {
        int bindPrt = 5000;
        String bingAddress = "127.0.0.1";
        ChannelContextManager manager = new ChannelContextManager();
        MetricExposerServer metricExposerServer = new MetricExposerServer(bindPrt, bingAddress, manager);
        metricExposerServer.start();
    }
}
