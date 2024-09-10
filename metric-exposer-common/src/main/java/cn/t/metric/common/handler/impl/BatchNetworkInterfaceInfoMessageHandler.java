package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.batch.BatchNetworkInterfaceInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

public class BatchNetworkInterfaceInfoMessageHandler extends AbstractMessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof BatchNetworkInterfaceInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }
    public BatchNetworkInterfaceInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
