package cn.t.metric.client.collector.impl;

import cn.t.metric.client.collector.NetWorkMetricCollector;
import cn.t.metric.common.constants.SystemConstants;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.util.CommandUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class IpNetWorkMetricCollectorImpl implements NetWorkMetricCollector {

    private static final String command = "ip -s link show up";

    @Override
    public BatchNetworkMetric bytePerSecond() {
        String output1 = CommandUtil.execute(command);
        LockSupport.parkNanos(ONE_SECOND);
        String output2 = CommandUtil.execute(command);
        List<NetworkMetric> networkMetricList1 = analyseNetworkMetricList(output1);
        List<NetworkMetric> networkMetricList2 = analyseNetworkMetricList(output2);
        minus(networkMetricList2, networkMetricList1);
        BatchNetworkMetric batchNetworkMetric = new BatchNetworkMetric();
        batchNetworkMetric.setNetworkMetricList(networkMetricList2);
        return batchNetworkMetric;
    }

    @Override
    public boolean test() {
        try {
            String output = CommandUtil.execute(command);
            String format = "command: %s" + SystemConstants.LINE_SEPARATOR + "output: %s%n";
            System.out.printf(format, command, output);
            return true;
        } catch (Throwable e) {
            return false;
        }
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
        for (int i = 0; i < output.length(); i+=SystemConstants.LINE_SEPARATOR_LENGTH) {
            int firstLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, i);
            String firstLine = output.substring(i, firstLineEndIndex);
            int firstColonIndex = firstLine.indexOf(":");
            int secondColonIndex = firstLine.indexOf(":", firstColonIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            String networkInterfaceName = firstLine.substring(firstColonIndex + SystemConstants.LINE_SEPARATOR_LENGTH, secondColonIndex).trim();
            int secondLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, firstLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            int thirdLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, secondLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            int forthLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, thirdLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            String forthLine = output.substring(thirdLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH, forthLineEndIndex);
            forthLine = forthLine.replaceAll("\\s{2,}", " ").trim();
            String[] elements = forthLine.split(" ");
            long receiveBytes = Long.parseLong(elements[0]);
            int fifthLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, forthLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            int sixthLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, fifthLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
            String sixthLine = output.substring(fifthLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH, sixthLineEndIndex);
            sixthLine = sixthLine.replaceAll("\\s{2,}", " ").trim();
            elements = sixthLine.split(" ");
            long sendBytes = Long.parseLong(elements[0]);;
            NetworkMetric networkMetric = new NetworkMetric();
            networkMetric.setInterfaceName(networkInterfaceName);
            networkMetric.setReceiveBytes(receiveBytes);
            networkMetric.setSendBytes(sendBytes);
            networkMetricList.add(networkMetric);
            i = sixthLineEndIndex;
        }
        return networkMetricList;
    }
}
