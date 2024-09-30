package cn.t.metric.client.channel;

import cn.t.metric.client.CmdRequestHandler;
import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.initializer.ChannelInitializer;

import java.nio.channels.Channel;

public class SocketChannelInitializer implements ChannelInitializer {
    @Override
    public void initChannel(ChannelContext ctx, Channel ch) throws Exception {
        ctx.getPipeline().addChannelHandlerLast(new CmdRequestHandler());
    }
}
