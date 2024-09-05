package cn.t.metric.common.message.metrics;

public class CpuLoadMetric {
    //cpu load
    private double systemCpuLoad;
    //cpu load average
    private double systemCpuLoadAverage;

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

    @Override
    public String toString() {
        return "CpuLoadMetric{" +
                "systemCpuLoad=" + systemCpuLoad +
                ", systemCpuLoadAverage=" + systemCpuLoadAverage +
                '}';
    }
}
