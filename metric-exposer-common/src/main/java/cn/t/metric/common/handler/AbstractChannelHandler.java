package cn.t.metric.common.handler;

import cn.t.metric.common.repository.SystemInfoRepository;

import java.nio.channels.SocketChannel;

public abstract class AbstractChannelHandler implements ChannelHandler<SocketChannel> {
    protected final SystemInfoRepository systemInfoRepository;

    public AbstractChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
