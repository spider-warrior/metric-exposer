package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.MessageHandler;

public class HeadMessageHandler implements MessageHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        channelContext.invokeNextChannelRead(msg);
    }

}
