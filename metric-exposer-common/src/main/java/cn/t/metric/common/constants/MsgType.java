package cn.t.metric.common.constants;

public enum MsgType {
    HEARTBEAT((byte)0),

    SYSTEM_INFO((byte)1),
    DISC_INFO((byte)2),
    NETWORK_INTERFACE_INFO((byte)3),

    SYSTEM_METRIC((byte)-1),
    CPU_METRIC((byte)-2),
    MEMORY_METRIC((byte)-3),
    DISC_METRIC((byte)-4),
    NETWORK_METRIC((byte)-5),

    CMD_REQUEST((byte)60),
    CMD_RESPONSE((byte)61),

    BATCH((byte)-128)
    ;
    public final byte value;


    MsgType(byte value) {
        this.value = value;
    }
}
