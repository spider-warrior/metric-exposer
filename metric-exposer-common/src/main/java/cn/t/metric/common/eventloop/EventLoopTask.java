package cn.t.metric.common.eventloop;

import cn.t.metric.common.channel.Promise;

import java.util.concurrent.Callable;

public class EventLoopTask<V> {

    private final Callable<V> command;
    private final long deadlineMills;
    private final Promise<V> promise;

    public long getDeadlineMills() {
        return deadlineMills;
    }

    public void run() {
        try {
            V v = command.call();
            promise.success(v);
        } catch (Throwable t) {
            promise.failure(t);
        }
    }

    public EventLoopTask(Callable<V> command, long deadlineMills, Promise<V> promise) {
        this.command = command;
        this.deadlineMills = deadlineMills;
        this.promise = promise;
    }
}
