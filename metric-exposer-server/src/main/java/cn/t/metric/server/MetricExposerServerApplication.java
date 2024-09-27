package cn.t.metric.server;

import cn.t.metric.common.bootstrap.ServerBootstrap;
import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.handler.ChannelHandler;

import java.nio.channels.SocketChannel;

public class MetricExposerServerApplication {
    public static void main(String[] args) throws Exception {
        int bindPrt = 5000;
        String bindAddress = "127.0.0.1";
        ChannelInitializer subChannelInitializer = (ctx, ch) -> ctx.getPipeline().addChannelHandlerLast(new ChannelHandler() {
            @Override
            public void ready(ChannelContext ctx) throws Exception {
                System.out.println("new connection: " + ((SocketChannel)ch).getRemoteAddress());
            }

            @Override
            public void read(ChannelContext ctx, Object msg) {
                System.out.println("read message: " + msg);
            }

            @Override
            public void write(ChannelContext ctx, Object msg) {
                System.out.println("write message: " + msg);
            }

            @Override
            public void close(ChannelContext ctx) throws Exception {
                System.out.println("close connection: "  + ((SocketChannel)ch).getRemoteAddress());
            }

            @Override
            public void error(ChannelContext ctx, Throwable t) {
                System.out.println("error: " + t);
            }
        });
        ServerBootstrap.bind(bindAddress, bindPrt, subChannelInitializer, new SingleThreadEventLoop(), new SingleThreadEventLoop());
    }
}
