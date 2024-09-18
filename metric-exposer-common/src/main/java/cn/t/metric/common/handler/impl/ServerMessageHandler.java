package cn.t.metric.common.handler.impl;

import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerMessageHandler {

    public static Collection<ChannelHandler> bizMessageHandlerList(SystemInfoRepository systemInfoRepository) {
        List<ChannelHandler> channelHandlerList = new ArrayList<>();
        channelHandlerList.add(new SystemInfoChannelHandler(systemInfoRepository));
        channelHandlerList.add(new DiscInfoChannelHandler(systemInfoRepository));
        channelHandlerList.add(new NetworkInterfaceInfoChannelHandler(systemInfoRepository));
        channelHandlerList.add(new SystemMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new CpuLoadMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new DiscMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new MemoryMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new NetworkMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new BatchDiscInfoChannelHandler(systemInfoRepository));
        channelHandlerList.add(new BatchNetworkInterfaceInfoChannelHandler(systemInfoRepository));
        channelHandlerList.add(new BatchDiscMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new BatchNetworkMetricChannelHandler(systemInfoRepository));
        channelHandlerList.add(new CmdResponseChannelHandler(systemInfoRepository));
        channelHandlerList.add(new HeartBeatChannelHandler(systemInfoRepository));
        return channelHandlerList;
    }

}
