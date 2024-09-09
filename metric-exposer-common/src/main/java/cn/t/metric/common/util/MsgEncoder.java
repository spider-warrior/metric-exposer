package cn.t.metric.common.util;

import cn.t.metric.common.constants.MsgType;
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
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MsgEncoder {

    private static final Map<Class<?>, Byte> classMsgTypeMap = Stream.of(
            new AbstractMap.SimpleEntry<>(HeartBeat.class, MsgType.HEARTBEAT.value),

            new AbstractMap.SimpleEntry<>(SystemInfo.class, MsgType.SYSTEM_INFO.value),
            new AbstractMap.SimpleEntry<>(DiscInfo.class, MsgType.DISC_INFO.value),
            new AbstractMap.SimpleEntry<>(NetworkInterfaceInfo.class, MsgType.NETWORK_INTERFACE_INFO.value),

            new AbstractMap.SimpleEntry<>(SystemMetric.class, MsgType.SYSTEM_METRIC.value),
            new AbstractMap.SimpleEntry<>(CpuLoadMetric.class, MsgType.CPU_METRIC.value),
            new AbstractMap.SimpleEntry<>(MemoryMetric.class, MsgType.MEMORY_METRIC.value),
            new AbstractMap.SimpleEntry<>(DiscMetric.class, MsgType.DISC_METRIC.value),
            new AbstractMap.SimpleEntry<>(NetworkMetric.class, MsgType.NETWORK_METRIC.value),
            new AbstractMap.SimpleEntry<>(BatchDiscInfo.class, MsgType.BATCH.value),
            new AbstractMap.SimpleEntry<>(BatchNetworkInterfaceInfo.class, MsgType.BATCH.value),
            new AbstractMap.SimpleEntry<>(BatchDiscMetric.class, MsgType.BATCH.value),
            new AbstractMap.SimpleEntry<>(BatchNetworkMetric.class, MsgType.BATCH.value),
            new AbstractMap.SimpleEntry<>(CmdRequest.class, MsgType.CMD_REQUEST.value),
            new AbstractMap.SimpleEntry<>(CmdResponse.class, MsgType.CMD_RESPONSE.value)
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    private static final Map<Byte, Integer> msgTypeLengthMap = Stream.of(
            new AbstractMap.SimpleEntry<>(MsgType.HEARTBEAT.value, 5),

            new AbstractMap.SimpleEntry<>(MsgType.SYSTEM_INFO.value, 1024),
            new AbstractMap.SimpleEntry<>(MsgType.DISC_INFO.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.NETWORK_INTERFACE_INFO.value, 256),

            new AbstractMap.SimpleEntry<>(MsgType.SYSTEM_METRIC.value, 1024),
            new AbstractMap.SimpleEntry<>(MsgType.CPU_METRIC.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.MEMORY_METRIC.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.DISC_METRIC.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.NETWORK_METRIC.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.CMD_REQUEST.value, 256),
            new AbstractMap.SimpleEntry<>(MsgType.CMD_RESPONSE.value, 1024),

            new AbstractMap.SimpleEntry<>(MsgType.BATCH.value, 1024)
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    private static final Map<String, byte[]> stringBytesCache = new WeakHashMap<>();

    public static ByteBuffer encode(Object message) {
        if(message instanceof SystemInfo) {
            return encode((SystemInfo)message);
        } else if(message instanceof DiscInfo) {
            return encode((DiscInfo)message);
        } else if(message instanceof NetworkInterfaceInfo) {
            return encode((NetworkInterfaceInfo)message);
        } else if(message instanceof SystemMetric) {
            return encode((SystemMetric)message);
        } else if(message instanceof CpuLoadMetric) {
            return encode((CpuLoadMetric)message);
        } else if(message instanceof DiscMetric) {
            return encode((DiscMetric)message);
        } else if(message instanceof MemoryMetric) {
            return encode((MemoryMetric)message);
        } else if(message instanceof NetworkMetric) {
            return encode((NetworkMetric)message);
        } else if(message instanceof HeartBeat) {
            return encode((HeartBeat)message);
        } else if(message instanceof BatchDiscInfo) {
            return encodeBatchDiscInfo((BatchDiscInfo)message);
        } else if(message instanceof BatchNetworkInterfaceInfo) {
            return encodeBatchNetworkInterfaceInfo((BatchNetworkInterfaceInfo) message);
        } else if(message instanceof BatchDiscMetric) {
            return encodeBatchDiscMetric((BatchDiscMetric)message);
        } else if(message instanceof BatchNetworkMetric) {
            return encodeBatchNetworkMetric((BatchNetworkMetric)message);
        } else if(message instanceof CmdRequest) {
            return encodeCmdRequest((CmdRequest)message);
        } else if(message instanceof CmdResponse) {
            return encodeCmdResponse((CmdResponse)message);
        } else {
            throw new RuntimeException("不支持编码的消息: " + message);
        }
    }

    public static ByteBuffer encodeCmdRequest(CmdRequest request) {
        ByteBuffer buffer = allocate(request);
        //cmd
        writeString(buffer, request.getCmd());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", request.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encodeCmdResponse(CmdResponse response) {
        ByteBuffer buffer = allocate(response);
        //output
        buffer = writeString(buffer, response.getOutput());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", response.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encodeBatchDiscInfo(BatchDiscInfo batchDiscInfo) {
        ByteBuffer buffer = allocate(batchDiscInfo);
        //content type
        buffer.put(MsgType.DISC_INFO.value);
        //content list
        writeDiscInfoCollection(buffer, batchDiscInfo.getDiscInfoList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", batchDiscInfo.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encodeBatchNetworkInterfaceInfo(BatchNetworkInterfaceInfo batchNetworkInterfaceInfo) {
        ByteBuffer buffer = allocate(batchNetworkInterfaceInfo);
        //content type
        buffer.put(MsgType.NETWORK_INTERFACE_INFO.value);
        //content list
        writeNetworkInterfaceInfoCollection(buffer, batchNetworkInterfaceInfo.getNetworkInterfaceInfoList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", batchNetworkInterfaceInfo.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encodeBatchDiscMetric(BatchDiscMetric batchDiscMetric) {
        ByteBuffer buffer = allocate(batchDiscMetric);
        //content type
        buffer.put(MsgType.DISC_METRIC.value);
        //content list
        writeDiscMetricCollection(buffer, batchDiscMetric.getDiscMetricList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", batchDiscMetric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encodeBatchNetworkMetric(BatchNetworkMetric batchNetworkMetric) {
        ByteBuffer buffer = allocate(batchNetworkMetric);
        //content type
        buffer.put(MsgType.NETWORK_METRIC.value);
        //content list
        writeNetworkMetricCollection(buffer, batchNetworkMetric.getNetworkMetricList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", batchNetworkMetric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    private static ByteBuffer encode(NetworkInterfaceInfo info) {
        ByteBuffer buffer = allocate(info);
        writeNetworkInterfaceInfo(buffer, info);
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", info.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    private static ByteBuffer encode(DiscInfo info) {
        ByteBuffer buffer = allocate(info);
        writeDiscInfo(buffer, info);
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", info.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encode(HeartBeat heartBeat) {
        ByteBuffer buffer = allocate(heartBeat);
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", heartBeat.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encode(SystemInfo info) {
        ByteBuffer buffer = allocate(info);
        //os name
        writeString(buffer, info.getOsName());
        //os arch
        writeString(buffer, info.getOsArch());
        //os version
        writeString(buffer, info.getOsVersion());
        //总物理内存大小
        buffer.putLong(info.getTotalPhysicalMemorySize());
        //剩余物理内存大小
        buffer.putLong(info.getFreePhysicalMemorySize());
        //总swap大小
        buffer.putLong(info.getTotalSwapSpaceSize());
        //剩余swap大小
        buffer.putLong(info.getFreeSwapSize());
        //processor数量
        buffer.putInt(info.getProcessorCount());
        //磁盘
        writeDiscInfoCollection(buffer, info.getDiscInfoList());
        //网卡
        writeNetworkInterfaceInfoCollection(buffer, info.getNetworkInterfaceInfoList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", info.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    private static ByteBuffer encode(SystemMetric metric) {
        ByteBuffer buffer = allocate(metric);
        //可用物理内存大小
        buffer.putLong(metric.getFreePhysicalMemorySize());
        //可用Swap大小
        buffer.putLong(metric.getFreeSwapSize());
        //系统cpu负载
        buffer.putDouble(metric.getSystemCpuLoad());
        //系统cpu平均负载
        buffer.putDouble(metric.getSystemCpuLoadAverage());
        //磁盘
        writeDiscMetricCollection(buffer, metric.getDiscMetricList());
        //网卡
        writeNetworkMetricCollection(buffer, metric.getNetworkMetricList());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", metric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encode(CpuLoadMetric metric) {
        ByteBuffer buffer = allocate(metric);
        //cpu load
        buffer.putDouble(metric.getSystemCpuLoad());
        //cpu load average
        buffer.putDouble(metric.getSystemCpuLoadAverage());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", metric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encode(DiscMetric metric) {
        ByteBuffer buffer = allocate(metric);
        //name
        writeString(buffer, metric.getName());
        //free size
        buffer.putLong(metric.getFreeSize());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", metric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    public static ByteBuffer encode(MemoryMetric metric) {
        ByteBuffer buffer = allocate(metric);
        //physical memory
        buffer.putLong(metric.getPhysicalMemoryFree());
        //swap
        buffer.putLong(metric.getSwapMemoryFree());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", metric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }
    public static ByteBuffer encode(NetworkMetric metric) {
        ByteBuffer buffer = allocate(metric);
        //name
        writeString(buffer, metric.getInterfaceName());
        //tx bytes
        buffer.putLong(metric.getSendBytes());
        //rx bytes
        buffer.putLong(metric.getReceiveBytes());
        //upload
        buffer.putInt(metric.getUploadBytePerSecond());
        //download
        buffer.putInt(metric.getDownloadBytePerSecond());
        //length
        writeLength(buffer);
        System.out.printf("encode %s, buffer size: %d%n", metric.getClass().getSimpleName(), buffer.position());
        return buffer;
    }

    private static void writeDiscInfoCollection(ByteBuffer buffer, Collection<DiscInfo> discInfoCollection) {
        if(discInfoCollection == null || discInfoCollection.isEmpty()) {
            buffer.put((byte)0);
        } else {
            buffer.put((byte)discInfoCollection.size());
            discInfoCollection.forEach(discInfo -> writeDiscInfo(buffer, discInfo));
        }
    }

    private static void writeDiscInfo(ByteBuffer buffer, DiscInfo discInfo) {
        //name
        writeString(buffer, discInfo.getName());
        //type
        writeString(buffer, discInfo.getType());
        //total size
        buffer.putLong(discInfo.getTotalSize());
    }

    private static void writeNetworkInterfaceInfoCollection(ByteBuffer buffer, Collection<NetworkInterfaceInfo> networkInterfaceInfoCollection) {
        if(networkInterfaceInfoCollection == null || networkInterfaceInfoCollection.isEmpty()) {
            buffer.put((byte)0);
        } else {
            buffer.put((byte)networkInterfaceInfoCollection.size());
            networkInterfaceInfoCollection.forEach(networkInterfaceInfo -> writeNetworkInterfaceInfo(buffer, networkInterfaceInfo));
        }
    }

    private static void writeNetworkInterfaceInfo(ByteBuffer buffer, NetworkInterfaceInfo networkInterfaceInfo) {
        //name
        writeString(buffer, networkInterfaceInfo.getInterfaceName());
        //ip
        writeString(buffer, networkInterfaceInfo.getIp());
        //mac
        writeString(buffer, networkInterfaceInfo.getMac());
    }

    private static void writeNetworkMetricCollection(ByteBuffer buffer, Collection<NetworkMetric> networkMetricCollection) {
        if(networkMetricCollection == null || networkMetricCollection.isEmpty()) {
            buffer.put((byte)0);
        } else {
            buffer.put((byte)networkMetricCollection.size());
            networkMetricCollection.forEach(networkMetric -> writeNetworkMetric(buffer, networkMetric));
        }
    }

    private static void writeNetworkMetric(ByteBuffer buffer, NetworkMetric networkMetric) {
        //name
        writeString(buffer, networkMetric.getInterfaceName());
        //tx bytes
        buffer.putLong(networkMetric.getSendBytes());
        //rx bytes
        buffer.putLong(networkMetric.getReceiveBytes());
        //upload
        buffer.putInt(networkMetric.getUploadBytePerSecond());
        //download
        buffer.putInt(networkMetric.getDownloadBytePerSecond());
    }

    private static void writeDiscMetricCollection(ByteBuffer buffer, Collection<DiscMetric> discMetricCollection) {
        if(discMetricCollection == null || discMetricCollection.isEmpty()) {
            buffer.put((byte)0);
        } else {
            buffer.put((byte)discMetricCollection.size());
            discMetricCollection.forEach(discMetric -> writeDiscMetric(buffer, discMetric));
        }
    }

    private static void writeDiscMetric(ByteBuffer buffer, DiscMetric discMetric) {
        //name
        writeString(buffer, discMetric.getName());
        //free size
        buffer.putLong(discMetric.getFreeSize());
    }

    private static ByteBuffer writeString(ByteBuffer buffer, String data) {
        byte[] bytes = stringBytesCache.computeIfAbsent(data, key -> data.getBytes());
        return ensureCapacity(buffer, bytes.length).putInt(bytes.length).put(bytes);
    }

    private static ByteBuffer ensureCapacity(ByteBuffer buffer, int size) {
        if (buffer.remaining() < size) {
            int newCapacity = buffer.capacity() * 2;
            ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
            // 将旧缓冲区的数据复制到新缓冲区
            buffer.flip(); // 切换到读模式
            newBuffer.put(buffer);
            return newBuffer;
        }
        return buffer;
    }

    private static ByteBuffer allocate(Object msg) {
        Byte msgType = classMsgTypeMap.get(msg.getClass());
        Integer length = msgTypeLengthMap.get(msgType);
        ByteBuffer buffer = ByteBuffer.allocate(length);
        //length
        buffer.putInt(0);
        //message type
        buffer.put(msgType);
        return buffer;
    }

    private static void writeLength(ByteBuffer buffer) {
        int length = buffer.position() - 4;
        int writePosition = buffer.position();
        buffer.position(0);
        buffer.putInt(length);
        buffer.position(writePosition);
    }
}
