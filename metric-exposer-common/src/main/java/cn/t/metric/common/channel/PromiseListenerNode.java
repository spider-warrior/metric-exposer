package cn.t.metric.common.channel;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class PromiseListenerNode<V> {

    private static final AtomicIntegerFieldUpdater<PromiseListenerNode> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(PromiseListenerNode.class, "consume");
    private final PromiseListener<V> listener;
    private volatile PromiseListenerNode<V> next;
    private volatile int consume = 0;

    public PromiseListener<V> getListener() {
        return listener;
    }

    public boolean tryConsume() {
        return STATE_UPDATER.compareAndSet(this, 0, 1);
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
