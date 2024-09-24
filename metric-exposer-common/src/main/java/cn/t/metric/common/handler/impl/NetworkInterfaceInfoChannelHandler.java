package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NetworkInterfaceInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof NetworkInterfaceInfo) {
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            PopulateUtil.populateNetworkInterfaceInfo(systemInfo, (NetworkInterfaceInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public NetworkInterfaceInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
