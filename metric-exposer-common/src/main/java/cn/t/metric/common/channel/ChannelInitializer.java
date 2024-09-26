package cn.t.metric.common.channel;

import java.nio.channels.NetworkChannel;

public abstract class ChannelInitializer <C extends NetworkChannel> {
    public abstract void initChannel(ChannelContext<C> ctx, C ch) throws Exception;
}
