package cn.t.metric.client;

import cn.t.metric.common.context.ChannelContext;

public class MessageHandlerAdapter {
    public void handle(ChannelContext channelContext, Object msg) {
        System.out.println("消息: " + msg);
    }
}
