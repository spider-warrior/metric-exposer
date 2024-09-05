package cn.t.metric.common.message.infos;

import java.util.List;

public class SystemInfo {
    //os name
    private String osName;
    //os arch
    private String osArch;
    //os version
    private String osVersion;
    //总物理内存大小
    private long totalPhysicalMemorySize;
    //总swap大小
    private long totalSwapSpaceSize;
    //cpu数量
    private int processorCount;
    //磁盘
    private List<DiscInfo> discInfoList;
    //网卡
    private List<NetworkInterfaceInfo> networkInterfaceInfoList;

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public long getTotalPhysicalMemorySize() {
        return totalPhysicalMemorySize;
    }

    public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
        this.totalPhysicalMemorySize = totalPhysicalMemorySize;
    }

    public long getTotalSwapSpaceSize() {
        return totalSwapSpaceSize;
    }

    public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
        this.totalSwapSpaceSize = totalSwapSpaceSize;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public void setProcessorCount(int processorCount) {
        this.processorCount = processorCount;
    }

    public List<DiscInfo> getDiscInfoList() {
        return discInfoList;
    }

    public void setDiscInfoList(List<DiscInfo> discInfoList) {
        this.discInfoList = discInfoList;
    }

    public List<NetworkInterfaceInfo> getNetworkInterfaceInfoList() {
        return networkInterfaceInfoList;
    }

    public void setNetworkInterfaceInfoList(List<NetworkInterfaceInfo> networkInterfaceInfoList) {
        this.networkInterfaceInfoList = networkInterfaceInfoList;
    }

    @Override
    public String toString() {
        return "SystemInfo{" +
                "osName='" + osName + '\'' +
                ", osArch='" + osArch + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", totalPhysicalMemorySize=" + totalPhysicalMemorySize +
                ", totalSwapSpaceSize=" + totalSwapSpaceSize +
                ", processorCount=" + processorCount +
                ", discInfoList=" + discInfoList +
                ", networkInterfaceInfoList=" + networkInterfaceInfoList +
                '}';
    }
}
