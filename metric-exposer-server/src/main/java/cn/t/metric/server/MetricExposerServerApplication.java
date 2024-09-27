package cn.t.metric.server;

import cn.t.metric.common.bootstrap.ServerBootstrap;
import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.channel.ChannelInitializer;
import cn.t.metric.common.channel.SingleThreadEventLoop;
import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.repository.SystemInfoRepository;

import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

public class MetricExposerServerApplication {
    public static void main(String[] args) throws Exception {
        int bindPrt = 5000;
        String bindAddress = "127.0.0.1";
        SystemInfoRepository systemInfoRepository  = new SystemInfoRepository();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            public void initChannel(ChannelContext ctx, Channel ch) throws Exception {
                ctx.getPipeline().addMessageHandlerLast(new ChannelHandler() {
                    @Override
                    public void ready(ChannelContext ctx) throws Exception {
                        System.out.println("new connection: " + ((SocketChannel)ch).getRemoteAddress());
                    }

                    @Override
                    public void read(ChannelContext ctx, Object msg) throws Exception {
                        System.out.println("read message: " + msg);
                    }

                    @Override
                    public void write(ChannelContext ctx, Object msg) throws Exception {
                        System.out.println("write message: " + msg);
                    }

                    @Override
                    public void error(ChannelContext ctx, Throwable t) throws Exception {
                        System.out.println("error: " + t);
                    }
                });
            }
        };
        ServerBootstrap.bind(bindAddress, bindPrt, channelInitializer, new SingleThreadEventLoop(), new SingleThreadEventLoop());
    }
}
