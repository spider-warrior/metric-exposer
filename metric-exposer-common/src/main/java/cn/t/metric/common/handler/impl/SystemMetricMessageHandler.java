package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.SystemMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class SystemMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof SystemMetric) {
            SystemMetric systemMetric = (SystemMetric)msg;
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setFreePhysicalMemorySize(systemMetric.getFreePhysicalMemorySize());
            systemInfo.setFreeSwapSize(systemMetric.getFreeSwapSize());
            systemInfo.setSystemCpuLoad(systemMetric.getSystemCpuLoad());
            systemMetric.setSystemCpuLoadAverage(systemMetric.getSystemCpuLoadAverage());
            //磁盘可用空间
            PopulateUtil.populateDiscInfo(systemInfo.getDiscInfoList(), systemMetric.getDiscMetricList());
            //网卡网速
            PopulateUtil.populateNetworkInterfaceInfo(systemInfo.getNetworkInterfaceInfoList(), systemMetric.getNetworkMetricList());
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }
    public SystemMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
