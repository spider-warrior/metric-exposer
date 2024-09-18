package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.CpuLoadMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

public class CpuLoadMetricChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof CpuLoadMetric) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setSystemCpuLoad(((CpuLoadMetric)msg).getSystemCpuLoad());
            systemInfo.setSystemCpuLoadAverage(((CpuLoadMetric)msg).getSystemCpuLoadAverage());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public CpuLoadMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
