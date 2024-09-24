package cn.t.metric.client;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.ChannelHandler;

import java.nio.channels.SocketChannel;

public class CmdRequestHandler implements ChannelHandler<SocketChannel> {

    @Override
    public void read(ChannelContext<SocketChannel> channelContext, Object msg) throws Exception {

    }
}
