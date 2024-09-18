package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.repository.SystemInfoRepository;

public class HeartBeatMessageHandler extends AbstractMessageHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) throws Exception {
        if(msg instanceof HeartBeat) {
            System.out.printf("心跳: 远程地址: %s%n", channelContext.getSocketChannel().getRemoteAddress());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public HeartBeatMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
