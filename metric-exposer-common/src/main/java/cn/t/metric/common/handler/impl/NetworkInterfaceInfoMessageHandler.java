package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.util.ArrayList;
import java.util.List;

public class NetworkInterfaceInfoMessageHandler extends AbstractMessageHandler {

    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof NetworkInterfaceInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryByIp(channelContext.getRemoteIp());
            if(systemInfo == null) {
                systemInfo = new SystemInfo();
                List<NetworkInterfaceInfo> discInfoList = new ArrayList<>();
                discInfoList.add((NetworkInterfaceInfo)msg);
                systemInfo.setNetworkInterfaceInfoList(discInfoList);
                systemInfoRepository.save(channelContext.getRemoteIp(), systemInfo);
            } else {
                PopulateUtil.populateNetworkInterfaceInfo(systemInfo, (NetworkInterfaceInfo)msg);
            }
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }

    public NetworkInterfaceInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
