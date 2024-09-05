package cn.t.metric.common.message.metrics;

public class MemoryMetric {
    private long physicalMemoryFree;
    private long swapMemoryFree;

    public long getPhysicalMemoryFree() {
        return physicalMemoryFree;
    }

    public void setPhysicalMemoryFree(long physicalMemoryFree) {
        this.physicalMemoryFree = physicalMemoryFree;
    }

    public long getSwapMemoryFree() {
        return swapMemoryFree;
    }

    public void setSwapMemoryFree(long swapMemoryFree) {
        this.swapMemoryFree = swapMemoryFree;
    }

    @Override
    public String toString() {
        return "MemoryMetric{" +
                "physicalMemoryFree=" + physicalMemoryFree +
                ", swapMemoryFree=" + swapMemoryFree +
                '}';
    }
}
