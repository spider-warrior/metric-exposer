package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

public class BatchDiscInfoMessageHandler extends AbstractMessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchDiscInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }
    public BatchDiscInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
