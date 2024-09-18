package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.ChannelHandler;

import java.util.Collection;

public class HeadChannelHandler implements ChannelHandler {

    private final Collection<ChannelHandler> channelHandlerCollection;

    @Override
    public void active(ChannelContext ctx) {
        ctx.addMessageHandler(channelHandlerCollection);
        ctx.addMessageHandler(new TailChannelHandler(ctx.getSocketChannel()));
    }

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        channelContext.invokeNextChannelRead(msg);
    }

    public HeadChannelHandler(Collection<ChannelHandler> channelHandlerCollection) {
        this.channelHandlerCollection = channelHandlerCollection;
    }
}
