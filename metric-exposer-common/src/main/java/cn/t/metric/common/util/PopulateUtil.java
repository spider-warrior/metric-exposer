package cn.t.metric.common.util;

import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.message.metrics.NetworkMetric;

import java.util.ArrayList;
import java.util.List;

public class PopulateUtil {
    public static void populateDiscInfo(SystemInfo systemInfo, DiscInfo info) {
        List<DiscInfo> discInfoList = systemInfo.getDiscInfoList();
        if(discInfoList == null) {
            discInfoList = new ArrayList<>();
            discInfoList.add(info);
            systemInfo.setDiscInfoList(discInfoList);
        } else {
            boolean replaced = false;
            for (int i = 0; i < discInfoList.size(); i++) {
                if(discInfoList.get(i).getName().equals(info.getName())) {
                    discInfoList.set(i, info);
                    replaced = true;
                    break;
                }
            }
            if(!replaced) {
                discInfoList.add(info);
            }
        }
    }

    public static void populateNetworkInterfaceInfo(SystemInfo systemInfo, NetworkInterfaceInfo info) {
        List<NetworkInterfaceInfo> networkInterfaceInfoList = systemInfo.getNetworkInterfaceInfoList();
        if(networkInterfaceInfoList == null) {
            networkInterfaceInfoList = new ArrayList<>();
            networkInterfaceInfoList.add(info);
            systemInfo.setNetworkInterfaceInfoList(networkInterfaceInfoList);
        } else {
            boolean replaced = false;
            for (int i = 0; i < networkInterfaceInfoList.size(); i++) {
                if(networkInterfaceInfoList.get(i).getMac().equals(info.getMac())) {
                    networkInterfaceInfoList.set(i, info);
                    replaced = true;
                    break;
                }
            }
            if(!replaced) {
                networkInterfaceInfoList.add(info);
            }
        }
    }

    public static void populateNetworkInterfaceInfo(List<NetworkInterfaceInfo> networkInterfaceInfoList, List<NetworkMetric> networkMetricList) {
        for (NetworkMetric networkMetric : networkMetricList) {
            boolean modified = false;
            for (NetworkInterfaceInfo networkInterfaceInfo : networkInterfaceInfoList) {
                if (networkMetric.getInterfaceName().equals(networkInterfaceInfo.getInterfaceName())) {
                    networkInterfaceInfo.setReceiveBytes(networkMetric.getReceiveBytes());
                    networkInterfaceInfo.setSendBytes(networkMetric.getSendBytes());
                    networkInterfaceInfo.setDownloadBytePerSecond(networkMetric.getDownloadBytePerSecond());
                    networkInterfaceInfo.setUploadBytePerSecond(networkMetric.getUploadBytePerSecond());
                    modified = true;
                    break;
                }
            }
            if(!modified) {
                networkInterfaceInfoList.add(toNetworkInterfaceInfo(networkMetric));
            }
        }
    }

    public static NetworkInterfaceInfo toNetworkInterfaceInfo(NetworkMetric networkMetric) {
        NetworkInterfaceInfo networkInterfaceInfo = new NetworkInterfaceInfo();
        networkInterfaceInfo.setInterfaceName(networkMetric.getInterfaceName());
        networkInterfaceInfo.setReceiveBytes(networkMetric.getReceiveBytes());
        networkInterfaceInfo.setSendBytes(networkMetric.getSendBytes());
        networkInterfaceInfo.setUploadBytePerSecond(networkMetric.getUploadBytePerSecond());
        networkInterfaceInfo.setDownloadBytePerSecond(networkMetric.getDownloadBytePerSecond());
        return networkInterfaceInfo;
    }

    public static void populateDiscInfo(List<DiscInfo> discInfoList, List<DiscMetric> discMetricList) {
        for (DiscMetric discMetric : discMetricList) {
            for (DiscInfo discInfo : discInfoList) {
                if(discMetric.getName().equals(discInfo.getName())) {
                    discInfo.setFreeSize(discMetric.getFreeSize());
                    break;
                }
            }
        }
    }
}
