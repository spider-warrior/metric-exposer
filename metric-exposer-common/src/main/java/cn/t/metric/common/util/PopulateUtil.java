package cn.t.metric.common.util;

import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.NetworkInterfaceInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.DiscMetric;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.message.metrics.SystemMetric;

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

    public static void populateDiscMetric(SystemMetric systemMetric, DiscMetric metric) {
        List<DiscMetric> discMetricList = systemMetric.getDiscMetricList();
        if(discMetricList == null) {
            discMetricList = new ArrayList<>();
            discMetricList.add(metric);
            systemMetric.setDiscMetricList(discMetricList);
        } else {
            boolean replaced = false;
            for (int i = 0; i < discMetricList.size(); i++) {
                if(discMetricList.get(i).getName().equals(metric.getName())) {
                    discMetricList.set(i, metric);
                    replaced = true;
                    break;
                }
            }
            if(!replaced) {
                discMetricList.add(metric);
            }
        }
    }

    public static void populateNetworkMetric(SystemMetric systemMetric, NetworkMetric metric) {
        List<NetworkMetric> networkMetricList = systemMetric.getNetworkMetricList();
        if(networkMetricList == null) {
            networkMetricList = new ArrayList<>();
            networkMetricList.add(metric);
            systemMetric.setNetworkMetricList(networkMetricList);
        } else {
            boolean replaced = false;
            for (int i = 0; i < networkMetricList.size(); i++) {
                if(networkMetricList.get(i).getInterfaceName().equals(metric.getInterfaceName())) {
                    networkMetricList.set(i, metric);
                    replaced = true;
                    break;
                }
            }
            if(!replaced) {
                networkMetricList.add(metric);
            }
        }
    }
}
