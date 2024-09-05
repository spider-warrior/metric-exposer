package cn.t.metric.common.message.metrics;

public class NetworkMetric {
    private String interfaceName;
    private long receiveBytes;
    private long sendBytes;
    private int uploadBytePerSecond;
    private int downloadBytePerSecond;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public long getReceiveBytes() {
        return receiveBytes;
    }

    public void setReceiveBytes(long receiveBytes) {
        this.receiveBytes = receiveBytes;
    }

    public long getSendBytes() {
        return sendBytes;
    }

    public void setSendBytes(long sendBytes) {
        this.sendBytes = sendBytes;
    }

    public int getUploadBytePerSecond() {
        return uploadBytePerSecond;
    }

    public void setUploadBytePerSecond(int uploadBytePerSecond) {
        this.uploadBytePerSecond = uploadBytePerSecond;
    }

    public int getDownloadBytePerSecond() {
        return downloadBytePerSecond;
    }

    public void setDownloadBytePerSecond(int downloadBytePerSecond) {
        this.downloadBytePerSecond = downloadBytePerSecond;
    }

    @Override
    public String toString() {
        return "NetworkMetric{" +
                "interfaceName='" + interfaceName + '\'' +
                ", receiveBytes=" + receiveBytes +
                ", sendBytes=" + sendBytes +
                ", uploadBytePerSecond=" + uploadBytePerSecond +
                ", downloadBytePerSecond=" + downloadBytePerSecond +
                '}';
    }
}
