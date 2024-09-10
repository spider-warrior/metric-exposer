package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.response.CmdResponse;
import cn.t.metric.common.repository.SystemInfoRepository;

public class CmdResponseMessageHandler extends AbstractMessageHandler {
    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof CmdResponse) {
            System.out.printf("cmd 输出内容: %s%n", ((CmdResponse)msg).getOutput());
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }
    public CmdResponseMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
