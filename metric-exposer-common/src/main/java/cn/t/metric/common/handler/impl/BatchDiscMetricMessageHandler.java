package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class BatchDiscMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchDiscMetric) {
            PopulateUtil.populateDiscInfo(systemInfoRepository.queryById(channelContext.getRemoteIp()).getDiscInfoList(), ((BatchDiscMetric)msg).getDiscMetricList());
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }
    public BatchDiscMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
