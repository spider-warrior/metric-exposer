package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class DiscMetricChannelHandler extends TypeMatchedChannelHandler<DiscMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, DiscMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
        List<DiscInfo> discInfoList = systemInfo.getDiscInfoList();
        for (DiscInfo discInfo : discInfoList) {
            if(discInfo.getName().equals(msg.getName())) {
                discInfo.setFreeSize(msg.getFreeSize());
                break;
            }
        }
    }

    public DiscMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
