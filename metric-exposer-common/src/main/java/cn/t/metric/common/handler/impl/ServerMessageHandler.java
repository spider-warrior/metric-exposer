package cn.t.metric.common.handler.impl;

import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerMessageHandler {

    public static Collection<MessageHandler> bizMessageHandlerList(SystemInfoRepository systemInfoRepository) {
        List<MessageHandler> messageHandlerList = new ArrayList<>();
        messageHandlerList.add(new SystemInfoMessageHandler(systemInfoRepository));
        messageHandlerList.add(new DiscInfoMessageHandler(systemInfoRepository));
        messageHandlerList.add(new NetworkInterfaceInfoMessageHandler(systemInfoRepository));
        messageHandlerList.add(new SystemMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new CpuLoadMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new DiscMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new MemoryMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new NetworkMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new BatchDiscInfoMessageHandler(systemInfoRepository));
        messageHandlerList.add(new BatchNetworkInterfaceInfoMessageHandler(systemInfoRepository));
        messageHandlerList.add(new BatchDiscMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new BatchNetworkMetricMessageHandler(systemInfoRepository));
        messageHandlerList.add(new CmdResponseMessageHandler(systemInfoRepository));
        messageHandlerList.add(new HeartBeatMessageHandler(systemInfoRepository));
        return messageHandlerList;
    }

}
