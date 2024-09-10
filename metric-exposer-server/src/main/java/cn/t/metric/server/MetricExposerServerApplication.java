package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.context.ChannelContextManager;
import cn.t.metric.common.message.request.CmdRequest;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.server.constants.MetricExposerServerStatus;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class MetricExposerServerApplication {
    public static void main(String[] args) {
        int bindPrt = 5000;
        String bingAddress = "127.0.0.1";
        ChannelContextManager channelContextManager = new ChannelContextManager();
        SystemInfoRepository systemInfoRepository  = new SystemInfoRepository();
        MetricExposerServer metricExposerServer = new MetricExposerServer(bindPrt, bingAddress, channelContextManager, systemInfoRepository);
        Thread inputThread = new Thread(() -> {
            while (metricExposerServer.status() != MetricExposerServerStatus.STARTED) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            }
            String ipCount = "1";
            String ipList = "2";
            String ipDetail = "3";
            String sendCmd = "4";
            String exitCmd = "0";

            Map<String, String> functions = new LinkedHashMap<>();
            functions.put(ipCount, "客户端统计");
            functions.put(ipList, "客户端列表");
            functions.put(ipDetail, "客户端详情");
            functions.put(sendCmd, "发送命令");
            functions.put(exitCmd, "退出");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("=============\n功能选项: ");
                for (Map.Entry<String, String> entry : functions.entrySet()) {
                    System.out.println(entry.getValue() + " : " + entry.getKey());
                }
                String command;
                do {
                    System.out.print("-------------\n输入: ");
                    command = scanner.nextLine();
                } while (!functions.containsKey(command));
                if (command.equals(exitCmd)) {
                    System.out.println("退出窗口命令行模式, byte....");
                    break;
                }
                System.out.println("输出>>> ");
                if(ipCount.equals(command)) {
                    System.out.println("客户端数量: " + channelContextManager.allChannelContext().size());
                } else if(ipList.equals(command)) {
                    System.out.println("ip列表: " + toIpList(channelContextManager.allChannelContext()));
                } else if(ipDetail.equals(command)) {
                    printIpList(channelContextManager.allChannelContext());
                    System.out.print("请输入ip: ");
                    String ip = scanner.nextLine();
                    System.out.println(systemInfoRepository.queryById(ip));
                } else if(sendCmd.equals(command)) {
                    System.out.print("请输入ip: ");
                    String ip = scanner.nextLine();
                    ChannelContext channelContext = getIpChannelContext(channelContextManager.allChannelContext(), ip);
                    if(channelContext == null) {
                        System.out.println("ip实例不存在!");
                    } else {
                        System.out.print("请输入命令: ");
                        String cmd = scanner.nextLine();
                        CmdRequest cmdRequest = new CmdRequest();
                        cmdRequest.setCmd(cmd);
                        channelContext.write(cmdRequest);
                    }
                }
            }
        });
        inputThread.start();
        metricExposerServer.start();
    }
    private static List<String> toIpList(Collection<ChannelContext> channelContexts) {
        List<String> ipList = new ArrayList<>();
        for (ChannelContext channelContext : channelContexts) {
            ipList.add(channelContext.getRemoteIp());
        }
        return ipList;
    }

    private static void printIpList(Collection<ChannelContext> channelContexts) {
        int index = 1;
        for (ChannelContext channelContext : channelContexts) {
            System.out.printf("%d.%s%n", index++, channelContext.getRemoteIp());
        }
    }

    private static ChannelContext getIpChannelContext(Collection<ChannelContext> channelContexts, String ip) {
        for (ChannelContext channelContext : channelContexts) {
            if(channelContext.getRemoteIp().equals(ip)) {
                return channelContext;
            }
        }
        return null;
    }
}
