package cn.t.metric.server.channel.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.HeartBeat;

import java.nio.channels.SocketChannel;

public class HeartBeatChannelHandler extends TypeMatchedChannelHandler<HeartBeat> {

    @Override
    public void doRead(ChannelContext ctx, HeartBeat msg) throws Exception {
        System.out.printf("心跳: 远程地址: %s%n", ((SocketChannel)ctx.getChannel()).getRemoteAddress());
    }

}
