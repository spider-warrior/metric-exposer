package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.ChannelHandler;

public class HeadChannelHandler implements ChannelHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        channelContext.invokeNextChannelRead(msg);
    }

}
