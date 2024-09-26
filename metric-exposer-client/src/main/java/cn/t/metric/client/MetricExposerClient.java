package cn.t.metric.client;

import cn.t.metric.client.constants.MetricExposerClientStatus;
import cn.t.metric.client.exception.MetricExposerClientException;
import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.MsgDecoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class MetricExposerClient {

    private final String serverHost;
    private final int serverPort;

    private ChannelContext channelContext;
    private MetricExposerClientStatus status;
    private boolean loopRead = true;

    public void start() {
        try (Selector selector = Selector.open()) {
            MetricCollector metricCollector = new MetricCollector();
            while (loopRead) {
                try(SocketChannel socketChannel = connect(serverHost, serverPort)) {
                    status = MetricExposerClientStatus.STARTED;
                    channelContext = new ChannelContext(socketChannel);
//                    channelContext.addMessageHandlerLast(ClientMessageHandler.handlerList());
                    //注册读取事件监听
                    socketChannel.register(selector, SelectionKey.OP_READ, new HashMap<>());
                    // 开启采集metric任务
                    metricCollector.startTask(channelContext);
                    // 循环读取消息
                    while (loopRead) {
                        int count = selector.select(3000);
                        if(count > 0) {
                            loopEvents(selector.selectedKeys());
                        }
                    }
                } catch (Exception e) {
                    if(loopRead) {
                        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    }
                } finally {
                    //停止采集任务
                    metricCollector.cancelTask();
                }
            }
        } catch (Exception e) {
            throw new MetricExposerClientException(e);
        } finally {
            status = MetricExposerClientStatus.STOPPED;
        }
    }

    private void loopEvents(Set<SelectionKey> selectionKeySet) throws IOException {
        Iterator<SelectionKey> it = selectionKeySet.iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            handleEvent(key);
        }
    }

    private void handleEvent(SelectionKey key) throws IOException {
        if(key.isValid()) {
            if(key.isReadable()) {
                handleReadEvent(key);
            } else {
                System.err.println("不能处理的事件: " + key);
            }
        } else {
            System.out.println("key invalid: " + key);
        }
    }

    private void handleReadEvent(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer readBuffer = ChannelUtil.getChannelBuffer(key);
        //服务端宕机此处抛出IO异常
        int length = sc.read(readBuffer);
        if(length > 0) {
            //convert to read mode
            readBuffer.flip();
            while (true) {
                Object msg = MsgDecoder.decode(readBuffer);
                if(msg == null) {
                    break;
                } else {
                    channelContext.getChannelPipeline().invokeChannelRead(msg);
                }
            }
            //convert to write mode
            readBuffer.compact();
        } else if(length < 0) {
            throw new IOException("服务端关闭连接");
        } else {
            System.out.println("读取0字节消息");
        }
    }

    private static SocketChannel connect(String ip, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(ip, port));
        System.out.println(ip + ":" + port + ",连接中....");
        while (!socketChannel.finishConnect()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
        }
        System.out.printf("连接已完成!, timestamp: %d%n", System.currentTimeMillis());
        return socketChannel;
    }

    public void stop() {
        if(this.loopRead) {
            this.loopRead = false;
        }
    }

    public MetricExposerClientStatus getStatus() {
        return status;
    }

    public MetricExposerClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }
}
