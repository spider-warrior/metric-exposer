package cn.t.metric.server.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.UnPooledHeapByteBuf;
import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.util.ExceptionUtil;

import java.nio.channels.SocketChannel;

public class TestHandler implements ChannelHandler {
    @Override
    public void ready(ChannelContext ctx) throws Exception {
        System.out.println("[channel-ready]accept new connection: " + ((SocketChannel)ctx.getChannel()).getRemoteAddress());
    }

    @Override
    public void read(ChannelContext ctx, Object msg) {
        UnPooledHeapByteBuf byteBuf = (UnPooledHeapByteBuf)msg;
//        if(true) {
//            throw new RuntimeException("on purpose");
//        }
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(array);
        System.out.println("[channel-read]read message: " + new String(array));
    }

    @Override
    public void write(ChannelContext ctx, Object msg) {
        System.out.println("write message: " + msg);
    }

    @Override
    public void close(ChannelContext ctx) throws Exception {
        System.out.println("[channel-close]closed connection: "  + ((SocketChannel)ctx.getChannel()).getRemoteAddress());
    }

    @Override
    public void error(ChannelContext ctx, Throwable t) {
        System.out.println("[channel-error]: " + ExceptionUtil.getErrorMessage(t));
    }
}
