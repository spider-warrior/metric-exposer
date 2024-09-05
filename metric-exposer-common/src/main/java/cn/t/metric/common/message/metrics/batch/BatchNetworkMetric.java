package cn.t.metric.common.message.metrics.batch;

import cn.t.metric.common.message.metrics.NetworkMetric;

import java.util.List;

public class BatchNetworkMetric {
    private List<NetworkMetric> networkMetricList;

    public List<NetworkMetric> getNetworkMetricList() {
        return networkMetricList;
    }

    public void setNetworkMetricList(List<NetworkMetric> networkMetricList) {
        this.networkMetricList = networkMetricList;
    }

    @Override
    public String toString() {
        return "BatchNetworkMetric{" +
                "networkMetricList=" + networkMetricList +
                '}';
    }
}
