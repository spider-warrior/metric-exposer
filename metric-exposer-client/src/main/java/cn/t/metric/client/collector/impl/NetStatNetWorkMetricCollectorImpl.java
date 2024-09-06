package cn.t.metric.client.collector.impl;

import cn.t.metric.client.collector.NetWorkMetricCollector;
import cn.t.metric.common.constants.SystemConstants;
import cn.t.metric.common.message.metrics.NetworkMetric;
import cn.t.metric.common.message.metrics.batch.BatchNetworkMetric;
import cn.t.metric.common.util.CommandUtil;

import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

public class NetStatNetWorkMetricCollectorImpl implements NetWorkMetricCollector {

    private static final String command = "netstat -e";
//    private static final String command = "wmic path Win32_PerfRawData_Tcpip_NetworkInterface get BytesReceivedPersec,BytesSentPersec";
//    private static final String command = "netsh interface ip show subinterfaces";

    @Override
    public BatchNetworkMetric bytePerSecond() {
        String output1 = CommandUtil.execute(command);
        LockSupport.parkNanos(ONE_SECOND);
        String output2 = CommandUtil.execute(command);
        NetworkMetric networkMetric1 = analyseNetworkMetricList(output1);
        NetworkMetric networkMetric2 = analyseNetworkMetricList(output2);
        minus(networkMetric2, networkMetric1);
        BatchNetworkMetric batchNetworkMetric = new BatchNetworkMetric();
        batchNetworkMetric.setNetworkMetricList(Collections.singletonList(networkMetric2));
        return batchNetworkMetric;
    }

    private NetworkMetric analyseNetworkMetricList(String output) {
        NetworkMetric networkMetric = new NetworkMetric();
        int firstLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR);
        int secondLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, firstLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
        int thirdLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, secondLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
        int forthLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, thirdLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
        int fifLineEndIndex = output.indexOf(SystemConstants.LINE_SEPARATOR, forthLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH);
        String fifLine = output.substring(forthLineEndIndex + SystemConstants.LINE_SEPARATOR_LENGTH, fifLineEndIndex);
        fifLine = fifLine.replaceAll("\\s{2,}", " ").trim();
        String[] elements = fifLine.split(" ");
        networkMetric.setInterfaceName("___global___");
        networkMetric.setReceiveBytes(Long.parseLong(elements[1]) / 8);
        networkMetric.setSendBytes(Long.parseLong(elements[2]) / 8);
        return networkMetric;
    }

    private void minus(NetworkMetric lastNetworkMetric, NetworkMetric penultimateNetworkMetric) {
        lastNetworkMetric.setUploadBytePerSecond((int)(lastNetworkMetric.getSendBytes() - penultimateNetworkMetric.getSendBytes()));
        lastNetworkMetric.setDownloadBytePerSecond((int)(lastNetworkMetric.getReceiveBytes() - penultimateNetworkMetric.getReceiveBytes()));
    }

    @Override
    public boolean test() {
        try {
            CommandUtil.execute(command);
//            String output = CommandUtil.execute(command);
//            String format = "command: %s%n output:%s%n";
//            System.out.printf(format, command, output);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
