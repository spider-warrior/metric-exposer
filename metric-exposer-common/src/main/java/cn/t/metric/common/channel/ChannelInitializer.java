package cn.t.metric.common.channel;

import java.nio.channels.Channel;

@FunctionalInterface
public interface ChannelInitializer {
    void initChannel(ChannelContext ctx, Channel ch) throws Exception;
}
