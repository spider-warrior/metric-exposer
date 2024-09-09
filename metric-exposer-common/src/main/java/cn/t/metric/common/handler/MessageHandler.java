package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;

public interface MessageHandler {
    void handle(ChannelContext channelContext, Object msg) throws Exception;
}
