package cn.t.metric.common.channel;

public interface PromiseListener<V> {
    void success(V result);
    void failure(Throwable throwable);
}
