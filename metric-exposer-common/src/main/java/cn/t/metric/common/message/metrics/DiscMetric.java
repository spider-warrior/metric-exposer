package cn.t.metric.common.message.metrics;

public class DiscMetric {
    private String name;
    private long freeSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(long freeSize) {
        this.freeSize = freeSize;
    }

    @Override
    public String toString() {
        return "DiscMetric{" +
                "name='" + name + '\'' +
                ", freeSize=" + freeSize +
                '}';
    }
}
