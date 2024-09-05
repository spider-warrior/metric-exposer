package cn.t.metric.common.message.metrics.batch;

import cn.t.metric.common.message.metrics.DiscMetric;

import java.util.List;

public class BatchDiscMetric {

    private List<DiscMetric> discMetricList;

    public List<DiscMetric> getDiscMetricList() {
        return discMetricList;
    }

    public void setDiscMetricList(List<DiscMetric> discMetricList) {
        this.discMetricList = discMetricList;
    }

    @Override
    public String toString() {
        return "BatchDiscMetric{" +
                "discMetricList=" + discMetricList +
                '}';
    }
}
