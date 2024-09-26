package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class DiscMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext ctx, Object msg) throws IOException {
        if(msg instanceof DiscMetric) {
            DiscMetric discMetric = (DiscMetric)msg;
            SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            List<DiscInfo> discInfoList = systemInfo.getDiscInfoList();
            for (DiscInfo discInfo : discInfoList) {
                if(discInfo.getName().equals(discMetric.getName())) {
                    discInfo.setFreeSize(discMetric.getFreeSize());
                    break;
                }
            }
        } else {
            ctx.getChannelPipeline().invokeNextChannelRead(msg);
        }
    }
    public DiscMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
