package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class BatchNetworkMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchNetworkMetric) {
            PopulateUtil.populateNetworkInterfaceInfo(systemInfoRepository.queryById(channelContext.getRemoteIp()).getNetworkInterfaceInfoList(), ((BatchNetworkMetric)msg).getNetworkMetricList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchNetworkMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
