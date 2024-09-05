package cn.t.metric.common.message.metrics.batch;

import cn.t.metric.common.message.infos.DiscInfo;

import java.util.List;

public class BatchDiscInfo {
    private List<DiscInfo> discInfoList;

    public List<DiscInfo> getDiscInfoList() {
        return discInfoList;
    }

    public void setDiscInfoList(List<DiscInfo> discInfoList) {
        this.discInfoList = discInfoList;
    }

    @Override
    public String toString() {
        return "BatchDiscInfo{" +
                "discInfoList=" + discInfoList +
                '}';
    }
}
