package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.repository.SystemInfoRepository;

public class HeartBeatChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) throws Exception {
        if(msg instanceof HeartBeat) {
            System.out.printf("心跳: 远程地址: %s%n", channelContext.getSocketChannel().getRemoteAddress());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public HeartBeatChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
