package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

public class SystemInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof SystemInfo) {
            systemInfoRepository.save(channelContext.getRemoteIp(), (SystemInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public SystemInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
