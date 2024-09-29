package cn.t.metric.server.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SystemInfoChannelHandler extends TypeMatchedChannelHandler<SystemInfo> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, SystemInfo msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        systemInfoRepository.save(socketAddress.getHostString(), msg);
    }

    public SystemInfoChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
