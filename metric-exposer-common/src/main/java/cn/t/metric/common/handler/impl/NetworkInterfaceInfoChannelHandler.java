package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

public class NetworkInterfaceInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof NetworkInterfaceInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            PopulateUtil.populateNetworkInterfaceInfo(systemInfo, (NetworkInterfaceInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public NetworkInterfaceInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
