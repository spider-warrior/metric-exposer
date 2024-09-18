package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.util.List;

public class DiscMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof DiscMetric) {
            DiscMetric discMetric = (DiscMetric)msg;
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            List<DiscInfo> discInfoList = systemInfo.getDiscInfoList();
            for (DiscInfo discInfo : discInfoList) {
                if(discInfo.getName().equals(discMetric.getName())) {
                    discInfo.setFreeSize(discMetric.getFreeSize());
                    break;
                }
            }
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public DiscMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
