package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.MessageHandler;

public class TailMessageHandler implements MessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        throw new MessageHandlerExecuteException("未知消息: " + msg);
    }
}
