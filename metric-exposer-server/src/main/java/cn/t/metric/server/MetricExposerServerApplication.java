package cn.t.metric.server;

import cn.t.metric.common.context.ChannelContextManager;

import java.util.Scanner;

public class MetricExposerServerApplication {
    public static void main(String[] args) {
        int bindPrt = 5000;
        String bingAddress = "127.0.0.1";
        ChannelContextManager manager = new ChannelContextManager();
        MetricExposerServer metricExposerServer = new MetricExposerServer(bindPrt, bingAddress, manager);
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                //todo 测试发送命令
                String line = scanner.nextLine();
            }
        });
        inputThread.start();
        metricExposerServer.start();
    }
}
