package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.HeadHandler;
import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.handler.TailHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.*;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkInterfaceInfo;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.message.response.CmdResponse;
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
        messageHandlerList.add(new HeadHandler());
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
        messageHandlerList.add(new CmdResponseMessageHandler());
        messageHandlerList.add(new HeartBeatMessageHandler());
        messageHandlerList.add(new TailHandler());
    }

    public void handle(ChannelContext channelContext, Object msg) {
        channelContext.invokeHandlerRead(messageHandlerList.iterator(), msg);
    }

    public class SystemInfoMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof SystemInfo) {
                ipSystemInfoMap.put(channelContext.getRemoteIp(), (SystemInfo)msg);
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class DiscInfoMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class NetworkInterfaceInfoMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class SystemMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof SystemMetric) {
                ipSystemMetricMap.put(channelContext.getRemoteIp(), (SystemMetric)msg);
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class CpuLoadMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class DiscMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class MemoryMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class NetworkMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class BatchDiscInfoMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchDiscInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    systemInfo.setDiscInfoList(((BatchDiscInfo)msg).getDiscInfoList());
                }
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class BatchNetworkInterfaceInfoMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchNetworkInterfaceInfo) {
                SystemInfo systemInfo = ipSystemInfoMap.get(channelContext.getRemoteIp());
                if(systemInfo == null) {
                    systemInfo = new SystemInfo();
                    systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
                    ipSystemInfoMap.put(channelContext.getRemoteIp(), systemInfo);
                } else {
                    systemInfo.setNetworkInterfaceInfoList(((BatchNetworkInterfaceInfo)msg).getNetworkInterfaceInfoList());
                }
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class BatchDiscMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
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
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public class BatchNetworkMetricMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof BatchNetworkMetric) {
                SystemMetric systemMetric = ipSystemMetricMap.get(channelContext.getRemoteIp());
                if(systemMetric == null) {
                    systemMetric = new SystemMetric();
                    systemMetric.setNetworkMetricList(((BatchNetworkMetric)msg).getNetworkMetricList());
                    ipSystemMetricMap.put(channelContext.getRemoteIp(), systemMetric);
                } else {
                    systemMetric.setNetworkMetricList(((BatchNetworkMetric)msg).getNetworkMetricList());
                }
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public static class CmdResponseMessageHandler implements MessageHandler  {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof CmdResponse) {
                System.out.printf("cmd 输出内容: %s%n", ((CmdResponse)msg).getOutput());
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }

    public static class HeartBeatMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) throws Exception {
            if(msg instanceof HeartBeat) {
                System.out.printf("心跳: 远程地址: %s%n", channelContext.getSocketChannel().getRemoteAddress());
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }
}
