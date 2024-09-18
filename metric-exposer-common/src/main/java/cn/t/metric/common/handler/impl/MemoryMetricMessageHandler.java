package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.MemoryMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

public class MemoryMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof MemoryMetric) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setFreePhysicalMemorySize(((MemoryMetric)msg).getPhysicalMemoryFree());
            systemInfo.setFreeSwapSize(((MemoryMetric)msg).getSwapMemoryFree());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public MemoryMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
