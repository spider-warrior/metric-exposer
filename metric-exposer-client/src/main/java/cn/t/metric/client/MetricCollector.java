package cn.t.metric.client;

import cn.t.metric.client.thread.ExposerThreadFactory;
import cn.t.metric.client.util.MetricCollectUtil;
import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.message.metrics.CpuLoadMetric;
import cn.t.metric.common.message.metrics.MemoryMetric;
import cn.t.metric.common.message.metrics.batch.BatchDiscMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MetricCollector {

    private static final ScheduledExecutorService scheduledExecutorService =  Executors.newScheduledThreadPool(2, new ExposerThreadFactory("exposer-client-"));
    private static final List<ScheduledFuture<?>> scheduledFutureListList = new ArrayList<>(4);

    public void startTask(ChannelContext channelContext) {
        SystemInfo systemInfo = MetricCollectUtil.collectSystemInfo();
        if(channelContext.getSocketChannel().isOpen() && channelContext.getSocketChannel().isConnected()) {
            channelContext.invokeChannelWrite(systemInfo);
        }
        //cpu采集
        ScheduledFuture<?> cpuTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            CpuLoadMetric message = MetricCollectUtil.collectCpuMetric();
            if(channelContext.getSocketChannel().isOpen() && channelContext.getSocketChannel().isConnected()) {
                channelContext.invokeChannelWrite(message);
            }
        }, 0, 5, TimeUnit.SECONDS);
        scheduledFutureListList.add(cpuTaskFuture);
        //内存采集
        ScheduledFuture<?> memoryTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            MemoryMetric message = MetricCollectUtil.collectMemoryMetric();
            if(channelContext.getSocketChannel().isOpen() && channelContext.getSocketChannel().isConnected()) {
                channelContext.invokeChannelWrite(message);
            }
        }, 0, 5, TimeUnit.SECONDS);
        scheduledFutureListList.add(memoryTaskFuture);
        //network采集
        ScheduledFuture<?> networkTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            BatchNetworkMetric message = MetricCollectUtil.collectBatchMetric();
            if(channelContext.getSocketChannel().isOpen() && channelContext.getSocketChannel().isConnected()) {
                channelContext.invokeChannelWrite(message);
            }
        }, 0, 5, TimeUnit.SECONDS);
        scheduledFutureListList.add(networkTaskFuture);
        //磁盘采集
        ScheduledFuture<?> discTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            BatchDiscMetric message = MetricCollectUtil.collectBatchDiscMetric();
            if(channelContext.getSocketChannel().isOpen() && channelContext.getSocketChannel().isConnected()) {
                channelContext.invokeChannelWrite(message);
            }
        }, 0, 20, TimeUnit.SECONDS);
        scheduledFutureListList.add(discTaskFuture);
        System.out.println("MetricCollector已就绪....");
    }

    public void cancelTask() {
        if(!scheduledFutureListList.isEmpty()) {
            scheduledFutureListList.forEach(scheduledFuture -> scheduledFuture.cancel(true));
            scheduledFutureListList.clear();
        }
    }

}
