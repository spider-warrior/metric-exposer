package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class BatchDiscMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchDiscMetric) {
            PopulateUtil.populateDiscInfo(systemInfoRepository.queryById(channelContext.getRemoteIp()).getDiscInfoList(), ((BatchDiscMetric)msg).getDiscMetricList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchDiscMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
