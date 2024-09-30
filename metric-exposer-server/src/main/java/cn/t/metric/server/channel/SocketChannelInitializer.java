package cn.t.metric.server.channel;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.initializer.ChannelInitializer;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.server.channel.handler.*;

import java.nio.channels.Channel;

public class SocketChannelInitializer implements ChannelInitializer {

    private final SystemInfoRepository systemInfoRepository = new SystemInfoRepository();

    @Override
    public void initChannel(ChannelContext ctx, Channel ch) throws Exception {
        ctx.getPipeline().addChannelHandlerLast(new TestHandler());
//        ctx.getPipeline().addChannelHandlerLast(new DecodeHandler());
//        ctx.getPipeline().addChannelHandlerLast(new BatchDiscInfoChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new BatchDiscMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new BatchNetworkInterfaceInfoChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new BatchNetworkMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new CmdResponseChannelHandler());
//        ctx.getPipeline().addChannelHandlerLast(new CpuLoadMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new DiscInfoChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new DiscMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new HeartBeatChannelHandler());
//        ctx.getPipeline().addChannelHandlerLast(new MemoryMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new NetworkInterfaceInfoChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new NetworkMetricChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new SystemInfoChannelHandler(systemInfoRepository));
//        ctx.getPipeline().addChannelHandlerLast(new SystemMetricChannelHandler(systemInfoRepository));
    }
}
