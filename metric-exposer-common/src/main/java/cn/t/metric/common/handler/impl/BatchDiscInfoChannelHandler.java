package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchDiscInfoChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof BatchDiscInfo) {
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }
    public BatchDiscInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
