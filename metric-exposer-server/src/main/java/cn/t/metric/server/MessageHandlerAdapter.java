package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.*;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkInterfaceInfo;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.util.PopulateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandlerAdapter {
    private final Map<String, SystemInfo> ipSystemInfoMap = new ConcurrentHashMap<>();
    private final Map<String, SystemMetric> ipSystemMetricMap = new ConcurrentHashMap<>();
    private final List<MessageHandler> messageHandlerList = new ArrayList<>();

    {
        messageHandlerList.add(new SystemInfoMessageHandler());
        messageHandlerList.add(new DiscInfoMessageHandler());
        messageHandlerList.add(new NetworkInterfaceInfoMessageHandler());
        messageHandlerList.add(new SystemMetricMessageHandler());
        messageHandlerList.add(new CpuLoadMetricMessageHandler());
        messageHandlerList.add(new DiscMetricMessageHandler());
        messageHandlerList.add(new MemoryMetricMessageHandler());
        messageHandlerList.add(new NetworkMetricMessageHandler());
        messageHandlerList.add(new BatchDiscInfoMessageHandler());
        messageHandlerList.add(new BatchNetworkInterfaceInfoMessageHandler());
        messageHandlerList.add(new BatchDiscMetricMessageHandler());
        messageHandlerList.add(new BatchNetworkMetricMessageHandler());
        messageHandlerList.add(new HeartBeatMessageHandler());
    }

    public void handle(ChannelContext channelContext, Object msg) {
        boolean handled = false;
        for (MessageHandler messageHandler : messageHandlerList) {
            try {
                if(messageHandler.handle(channelContext, msg)) {
                    handled = true;
                    break;
                }
            } catch (Exception e) {
                throw new MessageHandlerExecuteException(e);
            }
        }
        if(!handled) {
            throw new MessageHandlerExecuteException("未知消息: " + msg);
        }
    }

    public class SystemInfoMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof SystemInfo) {
                ipSystemInfoMap.put(channelContext.getRemoteIp(), (SystemInfo)msg);
                return true;
            }
            return false;
        }
    }

    public class DiscInfoMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof DiscInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    List<DiscInfo> discInfoList = new ArrayList<>();
                    discInfoList.add((DiscInfo)msg);
                    systemInfo.setDiscInfoList(discInfoList);
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    PopulateUtil.populateDiscInfo(systemInfo, (DiscInfo)msg);
                }
                return true;
            }
            return false;
        }
    }

    public class NetworkInterfaceInfoMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof NetworkInterfaceInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    List<NetworkInterfaceInfo> discInfoList = new ArrayList<>();
                    discInfoList.add((NetworkInterfaceInfo)msg);
                    systemInfo.setNetworkInterfaceInfoList(discInfoList);
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    PopulateUtil.populateNetworkInterfaceInfo(systemInfo, (NetworkInterfaceInfo)msg);
                }
                return true;
            }
            return false;
        }
    }

    public class SystemMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof SystemMetric) {
                ipSystemMetricMap.put(channelContext.getRemoteIp(), (SystemMetric)msg);
                return true;
            }
            return false;
        }
    }

    public class CpuLoadMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof CpuLoadMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    systemMetric.setSystemCpuLoad(((CpuLoadMetric)msg).getSystemCpuLoad());
                    systemMetric.setSystemCpuLoadAverage(((CpuLoadMetric)msg).getSystemCpuLoadAverage());
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    systemMetric.setSystemCpuLoad(((CpuLoadMetric)msg).getSystemCpuLoad());
                    systemMetric.setSystemCpuLoadAverage(((CpuLoadMetric)msg).getSystemCpuLoadAverage());
                }
                return true;
            }
            return false;
        }
    }

    public class DiscMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof DiscMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    List<DiscMetric> discMetricList = new ArrayList<>();
                    discMetricList.add((DiscMetric)msg);
                    systemMetric.setDiscMetricList(discMetricList);
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    PopulateUtil.populateDiscMetric(systemMetric, (DiscMetric)msg);
                }
                return true;
            }
            return false;
        }
    }

    public class MemoryMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof MemoryMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    systemMetric.setFreePhysicalMemorySize(((MemoryMetric)msg).getPhysicalMemoryFree());
                    systemMetric.setFreeSwapSize(((MemoryMetric)msg).getSwapMemoryFree());
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    systemMetric.setFreePhysicalMemorySize(((MemoryMetric)msg).getPhysicalMemoryFree());
                    systemMetric.setFreeSwapSize(((MemoryMetric)msg).getSwapMemoryFree());
                }
                return true;
            }
            return false;
        }
    }

    public class NetworkMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof NetworkMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    List<NetworkMetric> networkMetricList = new ArrayList<>();
                    networkMetricList.add((NetworkMetric)msg);
                    systemMetric.setNetworkMetricList(networkMetricList);
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    PopulateUtil.populateNetworkMetric(systemMetric, (NetworkMetric)msg);
                }
                return true;
            }
            return false;
        }
    }

    public class BatchDiscInfoMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchDiscInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
                }
                return true;
            }
            return false;
        }
    }

    public class BatchNetworkInterfaceInfoMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchNetworkInterfaceInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
                }
                return true;
            }
            return false;
        }
    }

    public class BatchDiscMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchDiscMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    systemMetric.setDiscMetricList(((BatchDiscMetric)msg).getDiscMetricList());
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    systemMetric.setDiscMetricList(((BatchDiscMetric)msg).getDiscMetricList());
                }
                System.out.println("-----------------------------------------------------------------------------------------------");
                System.out.println("ipSystemInfoMap:\n" + ipSystemInfoMap);
                System.out.println("ipSystemMetricMap:\n" + ipSystemMetricMap);
                System.out.println("-----------------------------------------------------------------------------------------------");
                return true;
            }
            return false;
        }
    }

    public class BatchNetworkMetricMessageHandler implements MessageHandler {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchNetworkMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    systemMetric.setNetworkMetricList(((BatchNetworkMetric)msg).getNetworkMetricList());
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    systemMetric.setNetworkMetricList(((BatchNetworkMetric)msg).getNetworkMetricList());
                }
                return true;
            }
            return false;
        }
    }

    public static class HeartBeatMessageHandler implements MessageHandler  {
        @Override
        public boolean handle(ChannelContext channelContext, Object msg) throws Exception {
            if(msg instanceof HeartBeat) {
                System.out.printf("心跳: 远程地址: %s%n", channelContext.getSocketChannel().getRemoteAddress());
                return true;
            }
            return false;
        }
    }
}
