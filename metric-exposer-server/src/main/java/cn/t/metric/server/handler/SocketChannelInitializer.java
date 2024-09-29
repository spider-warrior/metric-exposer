package cn.t.metric.server.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.nio.channels.Channel;

public class SocketChannelInitializer implements ChannelInitializer {

    private final SystemInfoRepository systemInfoRepository = new SystemInfoRepository();

    @Override
    public void initChannel(ChannelContext ctx, Channel ch) throws Exception {
//        ctx.getPipeline().addChannelHandlerLast(new TestHandler());
        ctx.getPipeline().addChannelHandlerLast(new BatchDiscInfoChannelHandler(systemInfoRepository));
    }
}
