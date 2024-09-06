package cn.t.metric.client.collector;


import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;

import java.util.concurrent.TimeUnit;

public interface NetWorkMetricCollector {
    long ONE_SECOND = TimeUnit.SECONDS.toNanos(1);
    BatchNetworkMetric bytePerSecond();
    boolean test();
}
