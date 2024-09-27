package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class DiscInfoChannelHandler extends AbstractChannelHandler {

    @Override
    public void read(ChannelContext ctx, Object msg) throws IOException {
        if(msg instanceof DiscInfo) {
            SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
            PopulateUtil.populateDiscInfo(systemInfo, (DiscInfo)msg);
        } else {
            ctx.invokeNextChannelRead(msg);
        }
    }

    public DiscInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
