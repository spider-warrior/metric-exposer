package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class DiscInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof DiscInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            PopulateUtil.populateDiscInfo(systemInfo, (DiscInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public DiscInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
