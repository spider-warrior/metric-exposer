package cn.t.metric.common.message.infos;

public class NetworkInterfaceInfo {
    private String interfaceName;
    private String ip;
    private String mac;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "NetworkInterfaceInfo{" +
                "interfaceName='" + interfaceName + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
