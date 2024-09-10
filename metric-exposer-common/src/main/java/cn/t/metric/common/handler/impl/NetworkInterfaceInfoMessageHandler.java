package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class NetworkInterfaceInfoMessageHandler extends AbstractMessageHandler {

    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof NetworkInterfaceInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            PopulateUtil.populateNetworkInterfaceInfo(systemInfo, (NetworkInterfaceInfo)msg);
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }

    public NetworkInterfaceInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
