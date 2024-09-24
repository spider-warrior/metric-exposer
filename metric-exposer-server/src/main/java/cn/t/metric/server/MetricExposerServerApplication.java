package cn.t.metric.server;

import cn.t.metric.common.bootstrap.ServerBootstrap;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.impl.*;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class MetricExposerServerApplication {
    public static void main(String[] args) throws IOException {
        int bindPrt = 5000;
        String bingAddress = "127.0.0.1";
        SystemInfoRepository systemInfoRepository  = new SystemInfoRepository();
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(ChannelContext<SocketChannel> channelContext, SocketChannel ch) throws Exception {
                channelContext.addMessageHandlerFirst(new SystemInfoChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new DiscInfoChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new NetworkInterfaceInfoChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new SystemMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new CpuLoadMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new DiscMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new MemoryMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new NetworkMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new BatchDiscInfoChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new BatchNetworkInterfaceInfoChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new BatchDiscMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new BatchNetworkMetricChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new CmdResponseChannelHandler(systemInfoRepository));
                channelContext.addMessageHandlerFirst(new HeartBeatChannelHandler(systemInfoRepository));
            }
        };
        ServerBootstrap.bind(bingAddress, bindPrt, channelInitializer, new SingleThreadEventLoop(), new SingleThreadEventLoop());
    }
}
