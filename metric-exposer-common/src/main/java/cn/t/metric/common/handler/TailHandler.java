package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.MessageHandlerExecuteException;

public class TailHandler implements MessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        throw new MessageHandlerExecuteException("未知消息: " + msg);
    }
}
