package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.CpuLoadMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class CpuLoadMetricChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext<SocketChannel> ctx, Object msg) throws IOException {
        if(msg instanceof CpuLoadMetric) {
            SocketChannel socketChannel = ctx.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            systemInfo.setSystemCpuLoad(((CpuLoadMetric)msg).getSystemCpuLoad());
            systemInfo.setSystemCpuLoadAverage(((CpuLoadMetric)msg).getSystemCpuLoadAverage());
        } else {
            ctx.getChannelPipeline().invokeNextChannelRead(msg);
        }
    }

    public CpuLoadMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
