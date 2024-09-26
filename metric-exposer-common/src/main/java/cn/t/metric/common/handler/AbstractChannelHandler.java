package cn.t.metric.common.handler;

import cn.t.metric.common.repository.SystemInfoRepository;

public abstract class AbstractChannelHandler implements ChannelHandler {
    protected final SystemInfoRepository systemInfoRepository;

    public AbstractChannelHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
