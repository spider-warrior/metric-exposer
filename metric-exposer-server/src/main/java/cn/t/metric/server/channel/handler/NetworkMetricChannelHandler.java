package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class NetworkMetricChannelHandler extends TypeMatchedChannelHandler<NetworkMetric> {

    private final SystemInfoRepository systemInfoRepository;

    @Override
    public void doRead(ChannelContext ctx, NetworkMetric msg) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
        SystemInfo systemInfo = systemInfoRepository.queryById(socketAddress.getHostString());
        List<NetworkInterfaceInfo> networkInterfaceInfoList = systemInfo.getNetworkInterfaceInfoList();
        for (NetworkInterfaceInfo networkInterfaceInfo : networkInterfaceInfoList) {
            if(networkInterfaceInfo.getInterfaceName().equals(msg.getInterfaceName())) {
                networkInterfaceInfo.setReceiveBytes(msg.getReceiveBytes());
                networkInterfaceInfo.setSendBytes(msg.getSendBytes());
                networkInterfaceInfo.setDownloadBytePerSecond(msg.getDownloadBytePerSecond());
                networkInterfaceInfo.setUploadBytePerSecond(msg.getUploadBytePerSecond());
            }
        }
    }

    public NetworkMetricChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
