package cn.t.metric.common.constants;

public enum EventLoopStatus {
    NOT_STARTED((byte)0),
    STARTED((byte)1),
    SHUTTING_DOWN((byte)2),
    SHUTDOWN ((byte)3),
    ;
    public final byte value;

    EventLoopStatus(byte value) {
        this.value = value;
    }
    public static EventLoopStatus getEventLoopStatus (byte value) {
        for (EventLoopStatus status : values()) {
            if(status.value == value) {
                return status;
            }
        }
        return null;
    }
}
