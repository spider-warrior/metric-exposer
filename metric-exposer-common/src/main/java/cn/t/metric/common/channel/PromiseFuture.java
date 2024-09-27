package cn.t.metric.common.channel;

public class PromiseFuture<V> {
    private final V value;
    private final Throwable throwable;

    public boolean isSuccess() {
        return throwable == null;
    }

    public V get() {
        return value;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public PromiseFuture(V value) {
        this(value, null);
    }

    public PromiseFuture(Throwable throwable) {
        this(null, throwable);
    }

    public PromiseFuture(V value, Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }
}
