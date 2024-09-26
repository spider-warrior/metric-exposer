package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.util.ExceptionUtil;

import java.nio.channels.NetworkChannel;

public interface ChannelHandler<C extends NetworkChannel> {
    default void ready(ChannelContext<C> ctx) throws Exception {}
    void read(ChannelContext<C> channelContext, Object msg) throws Exception;
    default void write(ChannelContext<C> channelContext, Object msg) throws Exception {
        channelContext.invokeNextChannelWrite(msg);
    }
    default void close(ChannelContext<C> ctx) throws Exception {}
    default void error(ChannelContext<C> ctx, Throwable t) throws Exception {
        System.out.println(ExceptionUtil.getErrorMessage(t));
    }
}
