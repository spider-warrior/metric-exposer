package cn.t.metric.common.initializer;

import cn.t.metric.common.channel.ChannelContext;

import java.nio.channels.Channel;

@FunctionalInterface
public interface ChannelInitializer {
    void initChannel(ChannelContext ctx, Channel ch) throws Exception;
}
