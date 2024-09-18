package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.util.List;

public class NetworkMetricMessageHandler extends AbstractMessageHandler {
    @Override
    public void read(ChannelContext channelContext, Object msg) {
        if(msg instanceof NetworkMetric) {
            NetworkMetric networkMetric = (NetworkMetric)msg;
            SystemInfo systemInfo = systemInfoRepository.queryById(channelContext.getRemoteIp());
            List<NetworkInterfaceInfo> networkInterfaceInfoList = systemInfo.getNetworkInterfaceInfoList();
            for (NetworkInterfaceInfo networkInterfaceInfo : networkInterfaceInfoList) {
                if(networkInterfaceInfo.getInterfaceName().equals(networkMetric.getInterfaceName())) {
                    networkInterfaceInfo.setReceiveBytes(networkMetric.getReceiveBytes());
                    networkInterfaceInfo.setSendBytes(networkMetric.getSendBytes());
                    networkInterfaceInfo.setDownloadBytePerSecond(networkMetric.getDownloadBytePerSecond());
                    networkInterfaceInfo.setUploadBytePerSecond(networkMetric.getUploadBytePerSecond());
                }
            }
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public NetworkMetricMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
