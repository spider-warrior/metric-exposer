package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class DiscInfoMessageHandler extends AbstractMessageHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof DiscInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            PopulateUtil.populateDiscInfo(systemInfo, (DiscInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public DiscInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
