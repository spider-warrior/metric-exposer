package cn.t.metric.server.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.ChannelInitializer;

import java.nio.channels.Channel;

public class ServerHandlerInitializer implements ChannelInitializer {
    @Override
    public void initChannel(ChannelContext ctx, Channel ch) throws Exception {
        ctx.getPipeline().addChannelHandlerLast(new TestHandler());
    }
}
