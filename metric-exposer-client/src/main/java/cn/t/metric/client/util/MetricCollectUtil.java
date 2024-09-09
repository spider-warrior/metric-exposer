package cn.t.metric.client.util;

import cn.t.metric.client.collector.NetWorkMetricCollector;
import cn.t.metric.client.collector.impl.CatNetDevNetWorkMetricCollectorImpl;
import cn.t.metric.client.collector.impl.DefaultNetworkMetricCollector;
import cn.t.metric.client.collector.impl.IpNetWorkMetricCollectorImpl;
import cn.t.metric.client.collector.impl.NetStatNetWorkMetricCollectorImpl;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.CpuLoadMetric;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.message.metrics.MemoryMetric;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MetricCollectUtil {

    private static final OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final NetWorkMetricCollector netWorkMetricCollector = defaultNetWorkMetricCollector();
    private static final List<FileStore> fileStoreList = fileStoreList();
    private static final List<NetworkInterface> networkInterfaceList = networkInterfaceList();

    private static List<FileStore> fileStoreList() {
        List<FileStore> fileStoreList = new ArrayList<>();
        FileSystems.getDefault().getFileStores().forEach(store -> {
            try {
                if(!store.isReadOnly() && store.getTotalSpace() > 0 && !store.name().endsWith("fs")) {
                    fileStoreList.add(store);
                }
            } catch (Exception e) {
                System.out.printf("磁盘列表初始话失败, %s%n", e.getMessage());
            }
        });
        return fileStoreList;
    }

    private static List<NetworkInterface> networkInterfaceList() {
        List<NetworkInterface> networkInterfaceList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if(ni.isUp() && ni.getHardwareAddress() != null) {
                    Enumeration<InetAddress> inetAddressEnumeration = ni.getInetAddresses();
                    while (inetAddressEnumeration.hasMoreElements()) {
                        InetAddress inetAddress = inetAddressEnumeration.nextElement();
                        if(inetAddress instanceof Inet4Address) {
                            networkInterfaceList.add(ni);
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.printf("NetworkInterface采集失败, %s%n", e.getMessage());
        }
        return networkInterfaceList;
    }

    private static NetWorkMetricCollector defaultNetWorkMetricCollector() {
        String osName = systemMXBean.getName();
        if(osName.toLowerCase().contains("window")) {
            NetWorkMetricCollector collector = new NetStatNetWorkMetricCollectorImpl();
            if(!collector.test()) {
                System.out.println("未适配到NetWorkMetricCollector");
                collector = new DefaultNetworkMetricCollector();
            }
            return collector;
        } else {
            NetWorkMetricCollector collector = new CatNetDevNetWorkMetricCollectorImpl();
            if(!collector.test()) {
                collector = new IpNetWorkMetricCollectorImpl();
                if(!collector.test()) {
                    System.out.println("未适配到NetWorkMetricCollector");
                    collector = new DefaultNetworkMetricCollector();
                }
            }
            return collector;
        }
    }

    public static SystemInfo collectSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setOsName(systemMXBean.getName());
        systemInfo.setOsArch(systemMXBean.getArch());
        systemInfo.setOsVersion(systemMXBean.getVersion());
        systemInfo.setTotalPhysicalMemorySize(systemMXBean.getTotalPhysicalMemorySize());
        systemInfo.setFreePhysicalMemorySize(systemMXBean.getFreePhysicalMemorySize());
        systemInfo.setTotalSwapSpaceSize(systemMXBean.getTotalSwapSpaceSize());
        systemInfo.setFreeSwapSize(systemMXBean.getFreeSwapSpaceSize());
        systemInfo.setProcessorCount(Runtime.getRuntime().availableProcessors());
        systemInfo.setSystemCpuLoad(systemMXBean.getSystemCpuLoad());
        systemInfo.setSystemCpuLoadAverage(systemMXBean.getSystemLoadAverage());
        systemInfo.setDiscInfoList(collectDiscInfoList());
        systemInfo.setNetworkInterfaceInfoList(collectNetworkInterfaceInfoList());
        return systemInfo;
    }

    public static List<NetworkInterfaceInfo> collectNetworkInterfaceInfoList() {
        List<NetworkInterfaceInfo> networkInterfaceInfoList = new ArrayList<>();
        networkInterfaceList.forEach(networkInterface -> {
            NetworkInterfaceInfo networkInterfaceInfo = new NetworkInterfaceInfo();
            networkInterfaceInfo.setInterfaceName(networkInterface.getDisplayName());
            try {
                networkInterfaceInfo.setMac(bytesToMac(networkInterface.getHardwareAddress()));
            } catch (SocketException e) {
                System.err.println("网卡[mac]采集失败, displayName: " + networkInterface.getDisplayName());
            }
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = inetAddressEnumeration.nextElement();
                if(inetAddress instanceof Inet4Address) {
                    networkInterfaceInfo.setIp(inetAddress.getHostAddress());
                }
            }
            networkInterfaceInfoList.add(networkInterfaceInfo);
        });
        return networkInterfaceInfoList;
    }

    public static List<DiscInfo> collectDiscInfoList() {
        List<DiscInfo> discInfoList = new ArrayList<>(fileStoreList.size());
        fileStoreList.forEach(store -> {
            DiscInfo discInfo = new DiscInfo();
            discInfo.setName(store.name());
            discInfo.setType(store.type());
            try {
                discInfo.setTotalSize(store.getTotalSpace());
                discInfo.setFreeSize(store.getUsableSpace());
            } catch (IOException e) {
                System.err.println("磁盘[总大小]采集失败, storeName: " + store.name());
                discInfo.setTotalSize(-1);
            }
            discInfoList.add(discInfo);
        });
        return discInfoList;
    }

    public static MemoryMetric collectMemoryMetric() {
        MemoryMetric memoryMetric = new MemoryMetric();
        memoryMetric.setPhysicalMemoryFree(systemMXBean.getFreePhysicalMemorySize());
        memoryMetric.setSwapMemoryFree(systemMXBean.getFreeSwapSpaceSize());
        return memoryMetric;
    }

    public static CpuLoadMetric collectCpuMetric() {
        CpuLoadMetric cpuLoadMetric = new CpuLoadMetric();
        cpuLoadMetric.setSystemCpuLoad(systemMXBean.getSystemCpuLoad());
        cpuLoadMetric.setSystemCpuLoadAverage(systemMXBean.getSystemLoadAverage());
        return cpuLoadMetric;
    }

    public static BatchNetworkMetric collectBatchMetric() {
        return netWorkMetricCollector.bytePerSecond();
    }

    public static BatchDiscMetric collectBatchDiscMetric() {
        List<DiscMetric> discMetricList = new ArrayList<>(fileStoreList.size());
        BatchDiscMetric batchDiscMetric = new BatchDiscMetric();
        batchDiscMetric.setDiscMetricList(discMetricList);
        fileStoreList.forEach(store -> {
            DiscMetric discMetric = new DiscMetric();
            discMetric.setName(store.name());
            try { discMetric.setFreeSize(store.getUsableSpace());} catch (IOException e) {
                System.err.println("磁盘[可用大小]采集失败, storeName: " + store.name());
                discMetric.setFreeSize(-1);
            }
            discMetricList.add(discMetric);
        });
        return batchDiscMetric;
    }

    private static String bytesToMac(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(String.format("%02X", bytes[i])); // 将每个字节转为两位16进制数
            if (i != bytes.length - 1) {
                sb.append(":"); // 添加冒号
            }
        }
        return sb.toString().toLowerCase(); // 转为小写字母
    }

}
