package cn.t.metric.common.channel;

public class PromiseListenerNode<V> {
    private final PromiseListener<V> listener;
    private volatile PromiseListenerNode<V> next;
    private volatile boolean executed = false;

    public PromiseListener<V> getListener() {
        return listener;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void executed() {
        this.executed = true;
    }

    public PromiseListenerNode<V> getNext() {
        return next;
    }

    public void setNext(PromiseListenerNode<V> next) {
        this.next = next;
    }

    public PromiseListenerNode(PromiseListener<V> listener) {
        this.listener = listener;
    }
}
