package cn.t.metric.common.util;

import cn.t.metric.common.constants.MsgType;
import cn.t.metric.common.exception.DecodeException;
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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MsgDecoder {
    public static Object decode(ByteBuffer buffer) {
        if(buffer.remaining() < 4) {
            return null;
        }
        //mark position
        buffer.mark();
        int length = buffer.getInt();
        if(buffer.remaining() < length) {
            buffer.reset();
            return null;
        } else {
            //message type
            byte type = buffer.get();
            if(MsgType.SYSTEM_INFO.value == type) {
                return decodeSystemInfo(buffer);
            } else if (MsgType.DISC_INFO.value == type) {
                return decodeDiscInfo(buffer);
            } else if (MsgType.NETWORK_INTERFACE_INFO.value == type) {
                return decodeNetworkInterfaceInfo(buffer);
            } else if (MsgType.SYSTEM_METRIC.value == type) {
                return decodeSystemMetric(buffer);
            } else if (MsgType.CPU_METRIC.value == type) {
                return decodeCpuLoadMetric(buffer);
            } else if (MsgType.MEMORY_METRIC.value == type) {
                return decodeMemoryMetric(buffer);
            } else if (MsgType.DISC_METRIC.value == type) {
                return decodeDiscMetric(buffer);
            } else if (MsgType.NETWORK_METRIC.value == type) {
                return decodeNetworkMetric(buffer);
            } else if (MsgType.HEARTBEAT.value == type) {
                return decodeHeartBeat(buffer);
            } else if (MsgType.BATCH.value == type) {
                return decodeBatch(buffer);
            } else if (MsgType.CMD_REQUEST.value == type) {
                return decodeCmdRequest(buffer);
            }  else if (MsgType.CMD_RESPONSE.value == type) {
                return decodeCmdResponse(buffer);
            }  else {
                throw new DecodeException("解析失败,未知消息类型: " +type);
            }
        }
    }

    private static CmdRequest decodeCmdRequest(ByteBuffer buffer) {
        CmdRequest request = new CmdRequest();
        request.setCmd(analyseString(buffer));
        return request;
    }

    private static CmdResponse decodeCmdResponse(ByteBuffer buffer) {
        CmdResponse response = new CmdResponse();
        response.setOutput(analyseString(buffer));
        return response;
    }

    private static Object decodeBatch(ByteBuffer buffer) {
        byte type = buffer.get();
        if(MsgType.DISC_INFO.value == type) {
            return decodeBatchDiscInfo(buffer);
        } else if(MsgType.NETWORK_INTERFACE_INFO.value == type) {
            return decodeBatchNetworkInterfaceInfo(buffer);
        } else if(MsgType.DISC_METRIC.value == type) {
            return decodeBatchDiscMetric(buffer);
        } else if(MsgType.NETWORK_METRIC.value == type) {
            return decodeBatchNetworkMetric(buffer);
        } else {
            throw new RuntimeException("未知的消息类型: " + type);
        }
    }

    private static BatchDiscInfo decodeBatchDiscInfo(ByteBuffer buffer) {
        BatchDiscInfo batchDiscInfo = new BatchDiscInfo();
        List<DiscInfo> readDiscInfoList = decodeDiscInfoList(buffer);
        batchDiscInfo.setDiscInfoList(readDiscInfoList);
        return batchDiscInfo;
    }

    private static BatchNetworkInterfaceInfo decodeBatchNetworkInterfaceInfo(ByteBuffer buffer) {
        BatchNetworkInterfaceInfo batchNetworkInterfaceInfo = new BatchNetworkInterfaceInfo();
        List<NetworkInterfaceInfo> networkInterfaceInfoList = decodeNetworkInterfaceInfoList(buffer);
        batchNetworkInterfaceInfo.setNetworkInterfaceInfoList(networkInterfaceInfoList);
        return batchNetworkInterfaceInfo;
    }

    private static BatchDiscMetric decodeBatchDiscMetric(ByteBuffer buffer) {
        BatchDiscMetric batchDiscMetric = new BatchDiscMetric();
        batchDiscMetric.setDiscMetricList(decodeDiscMetricList(buffer));
        return batchDiscMetric;
    }

    public static BatchNetworkMetric decodeBatchNetworkMetric(ByteBuffer buffer) {
        BatchNetworkMetric batchNetworkMetric = new BatchNetworkMetric();
        batchNetworkMetric.setNetworkMetricList(decodeNetworkMetricList(buffer));
        return batchNetworkMetric;
    }

    private static List<DiscInfo> decodeDiscInfoList(ByteBuffer buffer) {
        byte size = buffer.get();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<DiscInfo> discInfoCollection = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                discInfoCollection.add(decodeDiscInfo(buffer));
            }
            return discInfoCollection;
        }
    }

    private static DiscInfo decodeDiscInfo(ByteBuffer buffer) {
        DiscInfo discInfo = new DiscInfo();
        //name
        discInfo.setName(analyseString(buffer));
        //type
        discInfo.setType(analyseString(buffer));
        //total size
        discInfo.setTotalSize(buffer.getLong());
        return discInfo;
    }

    private static List<NetworkInterfaceInfo> decodeNetworkInterfaceInfoList(ByteBuffer buffer) {
        byte size = buffer.get();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<NetworkInterfaceInfo> networkInterfaceInfoList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                networkInterfaceInfoList.add(decodeNetworkInterfaceInfo(buffer));
            }
            return networkInterfaceInfoList;
        }
    }

    private static NetworkInterfaceInfo decodeNetworkInterfaceInfo(ByteBuffer buffer) {
        NetworkInterfaceInfo networkInterfaceInfo = new NetworkInterfaceInfo();
        //name
        networkInterfaceInfo.setInterfaceName(analyseString(buffer));
        //ip
        networkInterfaceInfo.setIp(analyseString(buffer));
        //mac
        networkInterfaceInfo.setMac(analyseString(buffer));
        return networkInterfaceInfo;
    }

    private static List<DiscMetric> decodeDiscMetricList(ByteBuffer buffer) {
        byte size = buffer.get();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<DiscMetric> discMetricList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                discMetricList.add(decodeDiscMetric(buffer));
            }
            return discMetricList;
        }
    }

    private static DiscMetric decodeDiscMetric(ByteBuffer buffer) {
        DiscMetric discMetric = new DiscMetric();
        //name
        discMetric.setName(analyseString(buffer));
        //free size
        discMetric.setFreeSize(buffer.getLong());
        return discMetric;
    }

    private static List<NetworkMetric> decodeNetworkMetricList(ByteBuffer buffer) {
        byte size = buffer.get();
        if(size == 0) {
            return Collections.emptyList();
        } else {
            List<NetworkMetric> networkMetricList = new ArrayList<>();
            for (byte i = 0; i < size; i++) {
                networkMetricList.add(decodeNetworkMetric(buffer));
            }
            return networkMetricList;
        }
    }

    private static NetworkMetric decodeNetworkMetric(ByteBuffer buffer) {
        NetworkMetric networkMetric = new NetworkMetric();
        //name
        networkMetric.setInterfaceName(analyseString(buffer));
        //tx bytes
        networkMetric.setSendBytes(buffer.getLong());
        //rx bytes
        networkMetric.setReceiveBytes(buffer.getLong());
        //upload
        networkMetric.setUploadBytePerSecond(buffer.getInt());
        //download
        networkMetric.setDownloadBytePerSecond(buffer.getInt());
        return networkMetric;
    }

    public static HeartBeat decodeHeartBeat(ByteBuffer buffer) {
        return HeartBeat.DEFAULT;
    }

    private static SystemInfo decodeSystemInfo(ByteBuffer buffer) {
        SystemInfo systemInfo = new SystemInfo();
        //os name
        systemInfo.setOsName(analyseString(buffer));
        //os arch
        systemInfo.setOsArch(analyseString(buffer));
        //os version
        systemInfo.setOsVersion(analyseString(buffer));
        //总物理内存大小
        systemInfo.setTotalPhysicalMemorySize(buffer.getLong());
        //总swap大小
        systemInfo.setTotalSwapSpaceSize(buffer.getLong());
        //processor数量
        systemInfo.setProcessorCount(buffer.getInt());
        //磁盘
        systemInfo.setDiscInfoList(decodeDiscInfoList(buffer));
        //网卡
        systemInfo.setNetworkInterfaceInfoList(decodeNetworkInterfaceInfoList(buffer));
        return systemInfo;
    }

    public static SystemMetric decodeSystemMetric(ByteBuffer buffer) {
        SystemMetric systemMetric = new SystemMetric();
        ////可用物理内存大小
        systemMetric.setFreePhysicalMemorySize(buffer.getLong());
        //可用Swap大小
        systemMetric.setFreeSwapSize(buffer.getLong());
        //系统cpu负载
        systemMetric.setSystemCpuLoad(buffer.getDouble());
        //系统cpu平均负载
        systemMetric.setSystemCpuLoadAverage(buffer.getDouble());
        //磁盘
        systemMetric.setDiscMetricList(decodeDiscMetricList(buffer));
        //网卡
        systemMetric.setNetworkMetricList(decodeNetworkMetricList(buffer));
        return systemMetric;
    }
    public static CpuLoadMetric decodeCpuLoadMetric(ByteBuffer buffer) {
        CpuLoadMetric cpuLoadMetric = new CpuLoadMetric();
        //cpu load
        cpuLoadMetric.setSystemCpuLoad(buffer.getDouble());
        //cpu load average
        cpuLoadMetric.setSystemCpuLoadAverage(buffer.getDouble());
        return cpuLoadMetric;
    }
    public static MemoryMetric decodeMemoryMetric(ByteBuffer buffer) {
        MemoryMetric memoryMetric = new MemoryMetric();
        //physical memory
        memoryMetric.setPhysicalMemoryFree(buffer.getLong());
        //swap
        memoryMetric.setSwapMemoryFree(buffer.getLong());
        return memoryMetric;
    }


    private static String analyseString(ByteBuffer buf) {
        //name
        int length = buf.getInt();
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return new String(bytes);
    }
}
