package cn.t.metric.client.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ExposerThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ExposerThreadFactory(String poolName) {
        this.namePrefix = poolName + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(Thread.currentThread().getThreadGroup(), runnable, namePrefix + threadNumber.getAndIncrement(), 0);
    }
}
