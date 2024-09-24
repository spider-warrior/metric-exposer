package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.SystemMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SystemMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof SystemMetric) {
            SystemMetric systemMetric = (SystemMetric)msg;
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            systemInfo.setFreePhysicalMemorySize(systemMetric.getFreePhysicalMemorySize());
            systemInfo.setFreeSwapSize(systemMetric.getFreeSwapSize());
            systemInfo.setSystemCpuLoad(systemMetric.getSystemCpuLoad());
            systemMetric.setSystemCpuLoadAverage(systemMetric.getSystemCpuLoadAverage());
            //磁盘可用空间
            PopulateUtil.populateDiscInfo(systemInfo.getDiscInfoList(), systemMetric.getDiscMetricList());
            //网卡网速
            PopulateUtil.populateNetworkInterfaceInfo(systemInfo.getNetworkInterfaceInfoList(), systemMetric.getNetworkMetricList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public SystemMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
