package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchNetworkMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof BatchNetworkMetric) {
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            PopulateUtil.populateNetworkInterfaceInfo(systemInfoRepository.queryById(socketAddress.getHostString()).getNetworkInterfaceInfoList(), ((BatchNetworkMetric)msg).getNetworkMetricList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchNetworkMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
