package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.ChannelHandler;

public class TailChannelHandler implements ChannelHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        throw new MessageHandlerExecuteException("未知消息: " + msg);
    }
}
