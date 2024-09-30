package cn.t.metric.common.initializer;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.eventloop.SingleThreadEventLoop;
import cn.t.metric.common.handler.ConnectionAcceptorHandler;

import java.nio.channels.Channel;

public class ServerSocketChannelInitializer implements ChannelInitializer {

    private final ChannelInitializer subChannelInitializer;
    private final SingleThreadEventLoop workerLoop;

    @Override
    public void initChannel(ChannelContext ctx, Channel ch) {
        ctx.getPipeline().addChannelHandlerLast(new ConnectionAcceptorHandler(subChannelInitializer, workerLoop));
    }

    public ServerSocketChannelInitializer(ChannelInitializer subChannelInitializer, SingleThreadEventLoop workerLoop) {
        this.subChannelInitializer = subChannelInitializer;
        this.workerLoop = workerLoop;
    }
}
