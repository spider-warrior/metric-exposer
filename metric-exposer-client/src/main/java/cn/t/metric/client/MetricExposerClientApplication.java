package cn.t.metric.client;

public class MetricExposerClientApplication {
    public static void main(String[] args) {
        String serverHost = "127.0.0.1";
        int serverPort = 5000;
        MetricExposerClient metricExposerClient = new MetricExposerClient(serverHost, serverPort);
        metricExposerClient.start();
    }
}
