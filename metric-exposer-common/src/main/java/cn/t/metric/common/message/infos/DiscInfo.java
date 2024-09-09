package cn.t.metric.common.message.infos;

public class DiscInfo {
    private String name;
    private String type;
    private long totalSize;
    private long freeSize;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(long freeSize) {
        this.freeSize = freeSize;
    }

    @Override
    public String toString() {
        return "DiscInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", totalSize=" + totalSize +
                ", freeSize=" + freeSize +
                '}';
    }
}
