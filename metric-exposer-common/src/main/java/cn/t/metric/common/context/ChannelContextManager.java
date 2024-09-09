package cn.t.metric.common.context;

import cn.t.metric.common.util.ChannelUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ChannelContextManager {

    private static final long ttlMills = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    private final List<String> tempList = new ArrayList<>(20);

    private final PriorityBlockingQueue<ChannelContext> channelContextPriorityQueue = new PriorityBlockingQueue<>(10, (o1, o2) -> (int)(o1.getLastRwTime() - o2.getLastRwTime()));

    public void add(ChannelContext channelContext) {
        boolean success = channelContextPriorityQueue.offer(channelContext);
        if(!success) {
            throw new RuntimeException("channelContextManager#add(channelContext) failed, channelContext: " + channelContext);
        }
    }

    public void modify(ChannelContext channelContext) {
        boolean success = channelContextPriorityQueue.remove(channelContext);
        if(!success) {
            throw new RuntimeException("channelContext remove failed, channelContext: " + channelContext);
        }
        success = channelContextPriorityQueue.offer(channelContext);
        if(!success) {
            throw new RuntimeException("channelContextPriorityQueue#offer(channelContext) failed, channelContext: " + channelContext);
        }
    }

    public void examineExpiredChannelContext() {
        System.out.println("连接超时扫描,当前连接总数量: " + channelContextPriorityQueue.size());
        long now = System.currentTimeMillis();
        while (true) {
            ChannelContext channelContext = channelContextPriorityQueue.peek();
            if(channelContext == null) {
                break;
            } else {
                long lastRw = channelContext.getLastRwTime();
                if(now - lastRw < ttlMills) {
                    break;
                } else {
                    ChannelContext channelContextDoubleCheck = channelContextPriorityQueue.peek();
                    if(channelContext == channelContextDoubleCheck) {
                        long lastRwDoubleCheck = channelContextDoubleCheck.getLastRwTime();
                        if(now - lastRwDoubleCheck >= ttlMills) {
                            SocketChannel socketChannel = channelContext.getSocketChannel();
                            String remoteAddress = channelContext.getRemoteIp() + ":" + channelContext.getRemotePort();
                            if(socketChannel.isOpen()) {
                                try { socketChannel.close();} catch (IOException ignore) {}
                                System.out.printf("连接超时,断开连接! remoteAddress: %s timestamp: %d%n", remoteAddress, now);
                            }
                            boolean cleared = clearChannelContext(channelContext);
                            if(cleared) {
                                tempList.add(remoteAddress);
                            } else {
                                throw new RuntimeException("channelContext clear failed, channelContext: " + channelContext);
                            }
                        } else {
                            System.out.println("peek element upTime has changed, now: " + now +", lastRw: "+ lastRw +", current: " + lastRwDoubleCheck + ", channelContextDoubleCheck: " + channelContextDoubleCheck);
                        }
                    } else {
                        System.out.printf("peek element has changed, original: %s, current: %s%n", channelContext, channelContextDoubleCheck);
                    }
                }
            }
        }
        if(!tempList.isEmpty()) {
            System.out.printf("连接超时,清理连接列表: %s%n", tempList);
            tempList.clear();
        }
    }

    public void closeAllChannelContext() {
        while(!channelContextPriorityQueue.isEmpty()) {
            ChannelContext channelContext = channelContextPriorityQueue.poll();
            channelContext.close();
        }
    }

    public boolean clearChannelContext(ChannelContext channelContext) {
        return channelContextPriorityQueue.remove(channelContext);
    }
}
