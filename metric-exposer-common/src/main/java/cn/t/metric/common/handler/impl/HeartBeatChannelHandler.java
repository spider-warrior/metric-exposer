package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.nio.channels.SocketChannel;

public class HeartBeatChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws Exception {
        if(msg instanceof HeartBeat) {
            System.out.printf("心跳: 远程地址: %s%n", channelContext.getChannel().getRemoteAddress());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public HeartBeatChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
