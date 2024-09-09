package cn.t.metric.common.context;

import cn.t.metric.common.exception.ChannelContextInitException;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.MsgEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ChannelContext {

    private final SocketChannel socketChannel;
    private final String remoteIp;
    private final int remotePort;
    private long lastReadTime;
    private long lastWriteTime;
    private long lastRwTime;
    private Iterator<MessageHandler> it;

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

    public void invokeHandlerRead(Iterator<MessageHandler> it, Object msg) {
        this.it = it;
        this.invokeNextHandlerRead(msg);
    }

    public void invokeNextHandlerRead(Object msg) {
        try {
            it.next().handle(this, msg);
        } catch (Exception e) {
            throw new MessageHandlerExecuteException(e);
        }
    }
}
