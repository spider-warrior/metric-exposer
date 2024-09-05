package cn.t.metric.client.constants;

public enum MetricExposerClientStatus {
    INIT((byte)0),
    STARTED((byte)1),
    STOPPED((byte)2);
    public final byte value;

    MetricExposerClientStatus(byte value) {
        this.value = value;
    }

    public static MetricExposerClientStatus getMetricExposerClientStatus(byte value) {
        for (MetricExposerClientStatus status : values()) {
            if(status.value == value) {
                return status;
            }
        }
        return null;
    }
}
