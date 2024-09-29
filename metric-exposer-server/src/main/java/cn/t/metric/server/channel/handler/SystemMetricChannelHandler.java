package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.SystemMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SystemMetricChannelHandler extends TypeMatchedChannelHandler<SystemMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, SystemMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
        systemInfo.setFreePhysicalMemorySize(msg.getFreePhysicalMemorySize());
        systemInfo.setFreeSwapSize(msg.getFreeSwapSize());
        systemInfo.setSystemCpuLoad(msg.getSystemCpuLoad());
        msg.setSystemCpuLoadAverage(msg.getSystemCpuLoadAverage());
        //磁盘可用空间
        PopulateUtil.populateDiscInfo(systemInfo.getDiscInfoList(), msg.getDiscMetricList());
        //网卡网速
        PopulateUtil.populateNetworkInterfaceInfo(systemInfo.getNetworkInterfaceInfoList(), msg.getNetworkMetricList());
    }

    public SystemMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
