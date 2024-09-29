package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.UnPooledHeapByteBuf;
import cn.t.metric.common.constants.MsgType;
import cn.t.metric.common.exception.DecodeException;
import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.message.HeartBeat;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.*;
import cn.t.metric.common.message.metrics.batch.BatchDiscInfo;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkInterfaceInfo;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.message.request.CmdRequest;
import cn.t.metric.common.message.response.CmdResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecodeHandler implements ChannelHandler {
    @Override
    public void read(ChannelContext ctx, Object msg) throws Exception {
        UnPooledHeapByteBuf byteBuf = (UnPooledHeapByteBuf)msg;
        if(byteBuf.readableBytes() > 4) {
            int readerIndex = byteBuf.readerIndex();
            int length = byteBuf.readInt();
            if(byteBuf.readableBytes() < length) {
                byteBuf.readerIndex(readerIndex);
            } else {
                //message type
                byte type = byteBuf.readByte();
                Object decodedMsg;
                if(MsgType.SYSTEM_INFO.value == type) {
                    decodedMsg = decodeSystemInfo(byteBuf);
                } else if (MsgType.DISC_INFO.value == type) {
                    decodedMsg = decodeDiscInfo(byteBuf);
                } else if (MsgType.NETWORK_INTERFACE_INFO.value == type) {
                    decodedMsg = decodeNetworkInterfaceInfo(byteBuf);
                } else if (MsgType.SYSTEM_METRIC.value == type) {
                    decodedMsg = decodeSystemMetric(byteBuf);
                } else if (MsgType.CPU_METRIC.value == type) {
                    decodedMsg = decodeCpuLoadMetric(byteBuf);
                } else if (MsgType.MEMORY_METRIC.value == type) {
                    decodedMsg = decodeMemoryMetric(byteBuf);
                } else if (MsgType.DISC_METRIC.value == type) {
                    decodedMsg = decodeDiscMetric(byteBuf);
                } else if (MsgType.NETWORK_METRIC.value == type) {
                    decodedMsg = decodeNetworkMetric(byteBuf);
                } else if (MsgType.HEARTBEAT.value == type) {
                    decodedMsg = decodeHeartBeat(byteBuf);
                } else if (MsgType.BATCH.value == type) {
                    decodedMsg = decodeBatch(byteBuf);
                } else if (MsgType.CMD_REQUEST.value == type) {
                    decodedMsg = decodeCmdRequest(byteBuf);
                }  else if (MsgType.CMD_RESPONSE.value == type) {
                    decodedMsg = decodeCmdResponse(byteBuf);
                }  else {
                    throw new DecodeException("解析失败,未知消息类型: " +type);
                }
                ctx.invokeNextChannelRead(decodedMsg);
            }
        }
    }

    private static CmdRequest decodeCmdRequest(UnPooledHeapByteBuf byteBuf) {
        CmdRequest request = new CmdRequest();
        request.setCmd(analyseString(byteBuf));
        return request;
    }

    private static CmdResponse decodeCmdResponse(UnPooledHeapByteBuf byteBuf) {
        CmdResponse response = new CmdResponse();
        byte success = byteBuf.readByte();
        response.setSuccess(success != 0);
        response.setOutput(analyseString(byteBuf));
        return response;
    }

    private static Object decodeBatch(UnPooledHeapByteBuf byteBuf) {
        byte type = byteBuf.readByte();
        if(MsgType.DISC_INFO.value == type) {
            return decodeBatchDiscInfo(byteBuf);
        } else if(MsgType.NETWORK_INTERFACE_INFO.value == type) {
            return decodeBatchNetworkInterfaceInfo(byteBuf);
        } else if(MsgType.DISC_METRIC.value == type) {
            return decodeBatchDiscMetric(byteBuf);
        } else if(MsgType.NETWORK_METRIC.value == type) {
            return decodeBatchNetworkMetric(byteBuf);
        } else {
            throw new RuntimeException("未知的消息类型: " + type);
        }
    }

    private static BatchDiscInfo decodeBatchDiscInfo(UnPooledHeapByteBuf byteBuf) {
        BatchDiscInfo batchDiscInfo = new BatchDiscInfo();
        List<DiscInfo> readDiscInfoList = decodeDiscInfoList(byteBuf);
        batchDiscInfo.setDiscInfoList(readDiscInfoList);
        return batchDiscInfo;
    }

    private static BatchNetworkInterfaceInfo decodeBatchNetworkInterfaceInfo(UnPooledHeapByteBuf byteBuf) {
        BatchNetworkInterfaceInfo batchNetworkInterfaceInfo = new BatchNetworkInterfaceInfo();
        List<NetworkInterfaceInfo> networkInterfaceInfoList = decodeNetworkInterfaceInfoList(byteBuf);
        batchNetworkInterfaceInfo.setNetworkInterfaceInfoList(networkInterfaceInfoList);
        return batchNetworkInterfaceInfo;
    }

    private static BatchDiscMetric decodeBatchDiscMetric(UnPooledHeapByteBuf byteBuf) {
        BatchDiscMetric batchDiscMetric = new BatchDiscMetric();
        batchDiscMetric.setDiscMetricList(decodeDiscMetricList(byteBuf));
        return batchDiscMetric;
    }

    public static BatchNetworkMetric decodeBatchNetworkMetric(UnPooledHeapByteBuf byteBuf) {
        BatchNetworkMetric batchNetworkMetric = new BatchNetworkMetric();
        batchNetworkMetric.setNetworkMetricList(decodeNetworkMetricList(byteBuf));
        return batchNetworkMetric;
    }

    private static List<DiscInfo> decodeDiscInfoList(UnPooledHeapByteBuf byteBuf) {
        byte size = byteBuf.readByte();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<DiscInfo> discInfoCollection = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                discInfoCollection.add(decodeDiscInfo(byteBuf));
            }
            return discInfoCollection;
        }
    }

    private static DiscInfo decodeDiscInfo(UnPooledHeapByteBuf byteBuf) {
        DiscInfo discInfo = new DiscInfo();
        //name
        discInfo.setName(analyseString(byteBuf));
        //type
        discInfo.setType(analyseString(byteBuf));
        //total size
        discInfo.setTotalSize(byteBuf.readLong());
        //free size
        discInfo.setFreeSize(byteBuf.readLong());
        return discInfo;
    }

    private static List<NetworkInterfaceInfo> decodeNetworkInterfaceInfoList(UnPooledHeapByteBuf byteBuf) {
        byte size = byteBuf.readByte();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<NetworkInterfaceInfo> networkInterfaceInfoList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                networkInterfaceInfoList.add(decodeNetworkInterfaceInfo(byteBuf));
            }
            return networkInterfaceInfoList;
        }
    }

    private static NetworkInterfaceInfo decodeNetworkInterfaceInfo(UnPooledHeapByteBuf byteBuf) {
        NetworkInterfaceInfo networkInterfaceInfo = new NetworkInterfaceInfo();
        //name
        networkInterfaceInfo.setInterfaceName(analyseString(byteBuf));
        //ip
        networkInterfaceInfo.setIp(analyseString(byteBuf));
        //mac
        networkInterfaceInfo.setMac(analyseString(byteBuf));
        return networkInterfaceInfo;
    }

    private static List<DiscMetric> decodeDiscMetricList(UnPooledHeapByteBuf byteBuf) {
        byte size = byteBuf.readByte();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<DiscMetric> discMetricList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                discMetricList.add(decodeDiscMetric(byteBuf));
            }
            return discMetricList;
        }
    }

    private static DiscMetric decodeDiscMetric(UnPooledHeapByteBuf byteBuf) {
        DiscMetric discMetric = new DiscMetric();
        //name
        discMetric.setName(analyseString(byteBuf));
        //free size
        discMetric.setFreeSize(byteBuf.readLong());
        return discMetric;
    }

    private static List<NetworkMetric> decodeNetworkMetricList(UnPooledHeapByteBuf byteBuf) {
        byte size = byteBuf.readByte();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<NetworkMetric> networkMetricList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                networkMetricList.add(decodeNetworkMetric(byteBuf));
            }
            return networkMetricList;
        }
    }

    private static NetworkMetric decodeNetworkMetric(UnPooledHeapByteBuf byteBuf) {
        NetworkMetric networkMetric = new NetworkMetric();
        //name
        networkMetric.setInterfaceName(analyseString(byteBuf));
        //tx bytes
        networkMetric.setSendBytes(byteBuf.readLong());
        //rx bytes
        networkMetric.setReceiveBytes(byteBuf.readLong());
        //upload
        networkMetric.setUploadBytePerSecond(byteBuf.readInt());
        //download
        networkMetric.setDownloadBytePerSecond(byteBuf.readInt());
        return networkMetric;
    }

    public static HeartBeat decodeHeartBeat(UnPooledHeapByteBuf byteBuf) {
        return HeartBeat.DEFAULT;
    }

    private static SystemInfo decodeSystemInfo(UnPooledHeapByteBuf byteBuf) {
        SystemInfo systemInfo = new SystemInfo();
        //os name
        systemInfo.setOsName(analyseString(byteBuf));
        //os arch
        systemInfo.setOsArch(analyseString(byteBuf));
        //os version
        systemInfo.setOsVersion(analyseString(byteBuf));
        //总物理内存大小
        systemInfo.setTotalPhysicalMemorySize(byteBuf.readLong());
        //可用物理内存大小
        systemInfo.setFreePhysicalMemorySize(byteBuf.readLong());
        //总swap大小
        systemInfo.setTotalSwapSpaceSize(byteBuf.readLong());
        //可用Swap大小
        systemInfo.setFreeSwapSize(byteBuf.readLong());
        //processor数量
        systemInfo.setProcessorCount(byteBuf.readInt());
        //cpu load
        systemInfo.setSystemCpuLoad(byteBuf.readDouble());
        //cpu load average
        systemInfo.setSystemCpuLoadAverage(byteBuf.readDouble());
        //磁盘
        systemInfo.setDiscInfoList(decodeDiscInfoList(byteBuf));
        //网卡
        systemInfo.setNetworkInterfaceInfoList(decodeNetworkInterfaceInfoList(byteBuf));
        return systemInfo;
    }

    public static SystemMetric decodeSystemMetric(UnPooledHeapByteBuf byteBuf) {
        SystemMetric systemMetric = new SystemMetric();
        ////可用物理内存大小
        systemMetric.setFreePhysicalMemorySize(byteBuf.readLong());
        //可用Swap大小
        systemMetric.setFreeSwapSize(byteBuf.readLong());
        //系统cpu负载
        systemMetric.setSystemCpuLoad(byteBuf.readDouble());
        //系统cpu平均负载
        systemMetric.setSystemCpuLoadAverage(byteBuf.readDouble());
        //磁盘
        systemMetric.setDiscMetricList(decodeDiscMetricList(byteBuf));
        //网卡
        systemMetric.setNetworkMetricList(decodeNetworkMetricList(byteBuf));
        return systemMetric;
    }
    public static CpuLoadMetric decodeCpuLoadMetric(UnPooledHeapByteBuf byteBuf) {
        CpuLoadMetric cpuLoadMetric = new CpuLoadMetric();
        //cpu load
        cpuLoadMetric.setSystemCpuLoad(byteBuf.readDouble());
        //cpu load average
        cpuLoadMetric.setSystemCpuLoadAverage(byteBuf.readDouble());
        return cpuLoadMetric;
    }
    public static MemoryMetric decodeMemoryMetric(UnPooledHeapByteBuf byteBuf) {
        MemoryMetric memoryMetric = new MemoryMetric();
        //physical memory
        memoryMetric.setPhysicalMemoryFree(byteBuf.readLong());
        //swap
        memoryMetric.setSwapMemoryFree(byteBuf.readLong());
        return memoryMetric;
    }

    private static String analyseString(UnPooledHeapByteBuf byteBuf) {
        //name
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return new String(bytes);
    }
}
