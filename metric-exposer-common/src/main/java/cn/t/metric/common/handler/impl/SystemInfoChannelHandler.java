package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SystemInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws IOException {
        if(msg instanceof SystemInfo) {
            SocketChannel socketChannel = channelContext.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            systemInfoRepository.save(socketAddress.getHostString(), (SystemInfo)msg);
        } else {
            channelContext.invokeNextChannelRead(msg);
        }
    }

    public SystemInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
