package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;

public interface MessageHandler {
    boolean handle(ChannelContext channelContext, Object msg) throws Exception;
}
