package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchDiscMetricChannelHandler extends TypeMatchedChannelHandler<BatchDiscMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, BatchDiscMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        PopulateUtil.populateDiscInfo(systemInfoRepository.queryById(socketAddress.getHostString()).getDiscInfoList(), (msg).getDiscMetricList());
    }

    public BatchDiscMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
