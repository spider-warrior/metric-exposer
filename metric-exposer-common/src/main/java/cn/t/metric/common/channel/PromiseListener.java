package cn.t.metric.common.channel;

@FunctionalInterface
public interface PromiseListener<V> {
    void operationComplete(PromiseFuture<V> future);
}
