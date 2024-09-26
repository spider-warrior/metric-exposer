package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class NetworkMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof NetworkMetric) {
            NetworkMetric networkMetric = (NetworkMetric)msg;
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
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
    public NetworkMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
