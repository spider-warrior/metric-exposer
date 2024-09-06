package cn.t.metric.client.collector.impl;

import cn.t.metric.client.collector.NetWorkMetricCollector;
import cn.t.metric.common.constants.SystemConstants;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.util.CommandUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class CatNetDevNetWorkMetricCollectorImpl implements NetWorkMetricCollector {

    private static final String command = "cat /proc/net/dev";

    @Override
    public BatchNetworkMetric bytePerSecond() {
        BatchNetworkMetric batchNetworkMetric = new BatchNetworkMetric();
        try {
            String content1 = readContent();
            LockSupport.parkNanos(ONE_SECOND);
            String content2 = readContent();
            List<NetworkMetric> networkMetricList1 = analyseNetworkMetricList(content1);
            List<NetworkMetric> networkMetricList2 = analyseNetworkMetricList(content2);
            minus(networkMetricList2, networkMetricList1);
            batchNetworkMetric.setNetworkMetricList(networkMetricList2);
        } catch (IOException e) {
            batchNetworkMetric.setNetworkMetricList(Collections.emptyList());
        }
        return batchNetworkMetric;
    }

    private void minus(List<NetworkMetric> lastList, List<NetworkMetric> penultimateList) {
        for (NetworkMetric networkMetric : lastList) {
            for (NetworkMetric metric : penultimateList) {
                if(networkMetric.getInterfaceName().equals(metric.getInterfaceName())) {
                    networkMetric.setUploadBytePerSecond((int)(networkMetric.getSendBytes() - metric.getSendBytes()));
                    networkMetric.setDownloadBytePerSecond((int)(networkMetric.getReceiveBytes() - metric.getReceiveBytes()));
                    break;
                }
            }
        }
    }

    private List<NetworkMetric> analyseNetworkMetricList(String output) {
        List<NetworkMetric> networkMetricList = new ArrayList<>();
        int firstLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR);
        int readerIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, firstLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH) + SystemConstants.LINE_SEPARATOR_LENGTH;
        while (readerIndex < output.length()) {
            int lineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, readerIndex);
            String line = output.substring(readerIndex, lineEndIndex);
            line = line.replaceAll("\\s{2,}", " ").trim();
//            System.out.println("line: " + line);
            String[] elements = line.split(" ");
            String networkInterfaceName = elements[0].substring(0, elements[0].length() - 1);
            long receiveBytes = Long.parseLong(elements[1]);
            long sendBytes = Long.parseLong(elements[9]);
            NetworkMetric networkMetric = new NetworkMetric();
            networkMetric.setInterfaceName(networkInterfaceName);
            networkMetric.setReceiveBytes(receiveBytes);
            networkMetric.setSendBytes(sendBytes);
            networkMetricList.add(networkMetric);
            readerIndex = lineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH;
        }
        return networkMetricList;
    }

    @Override
    public boolean test() {
        try {
            String output = readContent();
            String format = "command: %s" + SystemConstants.LINE_SEPARATOR + "output: %s%n";
            System.out.printf(format, command, output);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private String readContent() throws IOException {
        return CommandUtil.execute(command);
    }
}
