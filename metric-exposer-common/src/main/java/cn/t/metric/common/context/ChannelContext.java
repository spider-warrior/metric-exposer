package cn.t.metric.common.context;

import cn.t.metric.common.exception.ChannelContextInitException;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.MsgEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ChannelContext {

    private final SocketChannel socketChannel;
    private final String remoteIp;
    private final int remotePort;
    private long lastReadTime;
    private long lastWriteTime;
    private long lastRwTime;
    private final List<MessageHandler> messageHandlerList = new ArrayList<>();
    private Iterator<MessageHandler> channelActiveIt;
    private Iterator<MessageHandler> messageReadIt;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public long getLastWriteTime() {
        return lastWriteTime;
    }

    public void setLastWriteTime(long lastWriteTime) {
        this.lastWriteTime = lastWriteTime;
    }

    public long getLastRwTime() {
        return lastRwTime;
    }

    public void setLastRwTime(long lastRwTime) {
        this.lastRwTime = lastRwTime;
    }

    public void close() {
        if(this.socketChannel.isOpen()) {
            try { this.socketChannel.close(); } catch (IOException ignore) {}
        }
    }

    public ChannelContext(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        try {
            InetSocketAddress socketAddress = (InetSocketAddress)socketChannel.getRemoteAddress();
            this.remoteIp = socketAddress.getHostString();
            this.remotePort = socketAddress.getPort();
        } catch (IOException e) {
            throw new ChannelContextInitException(e);
        }
    }

    public void invokeChannelReady() {
        this.channelActiveIt = messageHandlerList.iterator();
        this.invokeNextChannelReady();
    }
    public void invokeNextChannelReady() {
        try {
            messageReadIt.next().active(this);
        } catch (Exception e) {
            throw new MessageHandlerExecuteException(e);
        }
    }


    public void write(Object msg) {
        try {
            ChannelUtil.write(socketChannel, MsgEncoder.encode(msg));
            long now = System.currentTimeMillis();
            this.lastWriteTime = now;
            this.lastRwTime = now;
        } catch (IOException e) {
            System.out.printf("异常类型：%s, 详情: %s%n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public void addMessageHandler(MessageHandler messageHandler) {
        this.messageHandlerList.add(messageHandler);
    }

    public void addMessageHandler(Collection<MessageHandler> messageHandlerCollection) {
        this.messageHandlerList.addAll(messageHandlerCollection);
    }

    public void invokeHandlerRead(Object msg) {
        this.messageReadIt = messageHandlerList.iterator();
        this.invokeNextHandlerRead(msg);
    }

    public void invokeNextHandlerRead(Object msg) {
        try {
            messageReadIt.next().handle(this, msg);
        } catch (Exception e) {
            throw new MessageHandlerExecuteException(e);
        }
    }
}
