package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.MemoryMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class MemoryMetricChannelHandler extends TypeMatchedChannelHandler<MemoryMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, MemoryMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
        systemInfo.setFreePhysicalMemorySize((msg).getPhysicalMemoryFree());
        systemInfo.setFreeSwapSize((msg).getSwapMemoryFree());
    }

    public MemoryMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
