package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.exception.MessageHandlerExecuteException;
import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.util.ChannelUtil;
import cn.t.metric.common.util.MsgEncoder;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class TailChannelHandler implements ChannelHandler {

    private final SocketChannel socketChannel;

    @Override
    public void read(ChannelContext channelContext, Object msg) {
        throw new MessageHandlerExecuteException("未知消息: " + msg);
    }

    @Override
    public void write(ChannelContext channelContext, Object msg) {
        try {
            ChannelUtil.write(socketChannel, MsgEncoder.encode(msg));
        } catch (IOException e) {
            System.out.printf("异常类型：%s, 详情: %s%n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public TailChannelHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
