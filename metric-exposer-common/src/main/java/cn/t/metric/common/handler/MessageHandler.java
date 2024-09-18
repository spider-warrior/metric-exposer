package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;

public interface MessageHandler {
    default void active(ChannelContext ctx) {
        //ignore
    }
    void handle(ChannelContext channelContext, Object msg) throws Exception;
}
