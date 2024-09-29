package cn.t.metric.common.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.util.ExceptionUtil;

public interface ChannelHandler {
    default void ready(ChannelContext ctx) throws Exception {}
    void read(ChannelContext ctx, Object msg) throws Exception;
    default void write(ChannelContext ctx, Object msg) throws Exception {
        ctx.invokeNextChannelWrite(msg);
    }
    default void close(ChannelContext ctx) throws Exception {}
    default void error(ChannelContext ctx, Throwable t) throws Exception {
        System.err.println(ExceptionUtil.getStackTrace(t));
    }
}
