package cn.t.metric.server.constants;

public enum MetricExposerServerStatus {
    INIT((byte)0),
    STARTED((byte)1),
    STOPPED((byte)2);
    public final byte value;

    MetricExposerServerStatus(byte value) {
        this.value = value;
    }

    public static MetricExposerServerStatus getMetricExposerServerStatus(byte value) {
        for (MetricExposerServerStatus status : values()) {
            if(status.value == value) {
                return status;
            }
        }
        return null;
    }
}
