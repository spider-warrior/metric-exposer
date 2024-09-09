package cn.t.metric.server;

import cn.t.metric.common.constants.ChannelAttrName;
import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.context.ChannelContextManager;
import cn.t.metric.common.handler.impl.BizMessageHandler;
import cn.t.metric.common.handler.impl.HeadMessageHandler;
import cn.t.metric.common.handler.impl.TailMessageHandler;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.MsgDecoder;
import cn.t.metric.server.constants.MetricExposerServerStatus;
import cn.t.metric.server.exception.MetricExposerServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MetricExposerServer {
    private final int bindPrt;
    private final String bingAddress;
    private final ChannelContextManager manager;
    private final long examinePeriod = TimeUnit.SECONDS.toMillis(3);
    private Selector openedSelector;
    private MetricExposerServerStatus status;
    private boolean loopRead = true;
    private long nextExamineTime = 0;


    public void start() {
        try (
                Selector selector = Selector.open();
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
        ) {
            openedSelector = selector;
            // 构建监听服务
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(bingAddress, bindPrt), 128);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.printf("metric-server启动, port: %d, timestamp: %d%n", bindPrt, System.currentTimeMillis());
            status = MetricExposerServerStatus.STARTED;
            while (loopRead) {
                // 读取消息
                int count = selector.select(3000);
                if(count > 0) {
                    loopEvents(selector.selectedKeys());
                }
                // 连接检查
                long now = System.currentTimeMillis();
                if(now > nextExamineTime) {
                    nextExamineTime = now + examinePeriod;
                    manager.examineExpiredChannelContext();
                }
            }
        } catch (Exception e) {
            throw new MetricExposerServerException(e);
        } finally {
            status = MetricExposerServerStatus.STOPPED;
            manager.closeAllChannelContext();
        }
    }

    private void loopEvents(Set<SelectionKey> selectionKeySet) {
        Iterator<SelectionKey> it = selectionKeySet.iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            try {
                handleEvent(key);
            } catch (Exception e) {
                System.out.printf("异常类型：%s, 详情: %s%n", e.getClass().getSimpleName(), e.getMessage());
                manager.clearChannelContext(ChannelUtil.getChannelContext(key));
                ChannelUtil.closeChannel(key);
            }
        }
    }

    private void handleEvent(SelectionKey key) throws IOException {
        if(key.isValid()) {
            if(key.isAcceptable()) {
                handleAcceptEvent(key);
            } else if(key.isReadable()) {
                handleReadEvent(key);
            } else {
                System.err.println("不能处理的事件: " + key);
            }
        }
    }

    private void handleAcceptEvent(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
        socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, false);
        ChannelContext channelContext = new ChannelContext(socketChannel);
        long now = System.currentTimeMillis();
        channelContext.setLastReadTime(now);
        channelContext.setLastRwTime(now);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ChannelAttrName.attrChannelContext, channelContext);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, attrs);
        //添加消息处理器
        channelContext.addMessageHandler(new HeadMessageHandler());
        channelContext.addMessageHandler(BizMessageHandler.bizMessageHandlerList());
        channelContext.addMessageHandler(new TailMessageHandler());
        manager.add(channelContext);
        System.out.printf("新建连接, 远程地址: %s, time: %d%n", socketChannel.getRemoteAddress(), System.currentTimeMillis());
    }

    private void handleReadEvent(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer readBuffer = ChannelUtil.getChannelBuffer(key);
        //服务端宕机此处抛出IO异常
        int length = sc.read(readBuffer);
        if(length > 0) {
            //convert to read mode
            readBuffer.flip();
            ChannelContext channelContext = ChannelUtil.getChannelContext(key);
            long now = System.currentTimeMillis();
            channelContext.setLastReadTime(now);
            channelContext.setLastRwTime(now);
            manager.modify(channelContext);
            while (true){
                Object message = MsgDecoder.decode(readBuffer);
                if(message == null) {
                    break;
                } else {
                    channelContext.invokeHandlerRead(message);
                }
            }
            //convert to write mode
            readBuffer.compact();
        } else if(length < 0) {
            throw new IOException("客户端关闭连接");
        } else {
            System.out.println("读取0字节消息");
        }
    }

    public void stop() {
        if(this.loopRead) {
            this.loopRead = false;
            this.openedSelector.wakeup();
        }
    }

    public MetricExposerServerStatus status() {
        return status;
    }

    public MetricExposerServer(int bindPrt, String bingAddress, ChannelContextManager manager) {
        this.bindPrt = bindPrt;
        this.bingAddress = bingAddress;
        this.manager = manager;
        this.status = MetricExposerServerStatus.INIT;
    }
}
