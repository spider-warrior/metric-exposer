package cn.t.metric.common.message.metrics;

import java.util.List;

public class SystemMetric {
    //可用物理内存大小
    private long freePhysicalMemorySize;
    //可用Swap大小
    private long freeSwapSize;
    //系统cpu负载
    private double systemCpuLoad;
    //系统cpu平均负载
    private double systemCpuLoadAverage;
    //磁盘
    private List<DiscMetric> discMetricList;
    //网卡
    private List<NetworkMetric> networkMetricList;

    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    public long getFreeSwapSize() {
        return freeSwapSize;
    }

    public void setFreeSwapSize(long freeSwapSize) {
        this.freeSwapSize = freeSwapSize;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public double getSystemCpuLoadAverage() {
        return systemCpuLoadAverage;
    }

    public void setSystemCpuLoadAverage(double systemCpuLoadAverage) {
        this.systemCpuLoadAverage = systemCpuLoadAverage;
    }

    public List<DiscMetric> getDiscMetricList() {
        return discMetricList;
    }

    public void setDiscMetricList(List<DiscMetric> discMetricList) {
        this.discMetricList = discMetricList;
    }

    public List<NetworkMetric> getNetworkMetricList() {
        return networkMetricList;
    }

    public void setNetworkMetricList(List<NetworkMetric> networkMetricList) {
        this.networkMetricList = networkMetricList;
    }

    @Override
    public String toString() {
        return "SystemMetric{" +
                "freePhysicalMemorySize=" + freePhysicalMemorySize +
                ", freeSwapSize=" + freeSwapSize +
                ", systemCpuLoad=" + systemCpuLoad +
                ", systemCpuLoadAverage=" + systemCpuLoadAverage +
                ", discMetricList=" + discMetricList +
                ", networkMetricList=" + networkMetricList +
                '}';
    }
}
