package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.batch.BatchNetworkInterfaceInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class BatchNetworkInterfaceInfoChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext ctx, Object msg) throws IOException {
        if(msg instanceof BatchNetworkInterfaceInfo) {
            SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
        } else {
            ctx.getChannelPipeline().invokeNextChannelRead(msg);
        }
    }
    public BatchNetworkInterfaceInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
