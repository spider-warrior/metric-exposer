package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchDiscMetricChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof BatchDiscMetric) {
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            PopulateUtil.populateDiscInfo(systemInfoRepository.queryById(socketAddress.getHostString()).getDiscInfoList(), ((BatchDiscMetric)msg).getDiscMetricList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchDiscMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
