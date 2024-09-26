package cn.t.metric.common.channel;

import java.nio.channels.Channel;

public abstract class ChannelInitializer <C extends Channel> {
    public abstract void initChannel(ChannelContext ctx, C ch) throws Exception;
}
