package cn.t.metric.common.channel;

import cn.t.metric.common.context.ChannelContext;

import java.nio.channels.NetworkChannel;

public abstract class ChannelInitializer <C extends NetworkChannel> {
    public abstract void initChannel(ChannelContext<C> ctx, C ch) throws Exception;
}
