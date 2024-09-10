package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.context.ChannelContextManager;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.util.*;

public class MetricExposerServerApplication {
    public static void main(String[] args) {
        int bindPrt = 5000;
        String bingAddress = "127.0.0.1";
        ChannelContextManager channelContextManager = new ChannelContextManager();
        SystemInfoRepository systemInfoRepository  = new SystemInfoRepository();
        MetricExposerServer metricExposerServer = new MetricExposerServer(bindPrt, bingAddress, channelContextManager, systemInfoRepository);
        Thread inputThread = new Thread(() -> {
            String ipList = "1";
            String ipDetail = "2";
            String sendCmd = "3";
            String exitCmd = "0";

            Map<String, String> functions = new LinkedHashMap<>();
            functions.put(ipList, "客户端列表");
            functions.put(ipDetail, "客户端详情");
            functions.put(sendCmd, "发送命令");
            functions.put(exitCmd, "退出");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("功能选项: ");
                for (Map.Entry<String, String> entry : functions.entrySet()) {
                    System.out.println(entry.getValue() + " : " + entry.getKey());
                }
                String command;
                do {
                    command = scanner.nextLine();
                } while (!functions.containsKey(command));
                if(ipList.equals(command)) {
                    System.out.println("ip列表");
                    System.out.println("----------------------------------");
                    printIpList(channelContextManager.allChannelContext());
                } else if(ipDetail.equals(command)) {
                    printIpList(channelContextManager.allChannelContext());
                    System.out.print("请输入ip: ");
                    String ip = scanner.nextLine();
                    System.out.println(systemInfoRepository.queryById(ip));
                } else if(sendCmd.equals(command)) {
                    System.out.println();
                } else {
                    break;
                }
            }
        });
        inputThread.start();
        metricExposerServer.start();
    }
    private static void printIpList(Collection<ChannelContext> channelContexts) {
        int index = 1;
        for (ChannelContext channelContext : channelContexts) {
            System.out.printf("%d.%s%n", index++, channelContext.getRemoteIp());
        }
    }
}
