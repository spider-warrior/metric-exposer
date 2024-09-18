package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

public class BatchDiscInfoChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchDiscInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchDiscInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
