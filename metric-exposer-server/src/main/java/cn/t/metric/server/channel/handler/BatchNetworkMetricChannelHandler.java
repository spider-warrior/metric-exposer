package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchNetworkMetricChannelHandler extends TypeMatchedChannelHandler<BatchNetworkMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, BatchNetworkMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        PopulateUtil.populateNetworkInterfaceInfo(systemInfoRepository.queryById(socketAddress.getHostString()).getNetworkInterfaceInfoList(), (msg).getNetworkMetricList());
    }

    public BatchNetworkMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
