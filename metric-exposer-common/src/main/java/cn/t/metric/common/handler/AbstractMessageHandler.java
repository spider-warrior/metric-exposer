package cn.t.metric.common.handler;

import cn.t.metric.common.repository.SystemInfoRepository;

public abstract class AbstractMessageHandler implements MessageHandler {
    protected final SystemInfoRepository systemInfoRepository;

    public AbstractMessageHandler(SystemInfoRepository systemInfoRepository) {
        this.systemInfoRepository = systemInfoRepository;
    }
}
