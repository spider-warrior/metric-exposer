package cn.t.metric.common.handler;

import cn.t.metric.common.context.ChannelContext;

public class HeadHandler implements MessageHandler {

    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        channelContext.invokeNextHandlerRead(msg);
    }

}
