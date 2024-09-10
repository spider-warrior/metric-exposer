package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

public class SystemInfoMessageHandler extends AbstractMessageHandler {

    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof SystemInfo) {
            systemInfoRepository.save(channelContext.getRemoteIp(), (SystemInfo)msg);
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }

    public SystemInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
