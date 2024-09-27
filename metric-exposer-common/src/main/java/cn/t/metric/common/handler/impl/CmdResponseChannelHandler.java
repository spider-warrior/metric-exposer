package cn.t.metric.common.handler.impl;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.AbstractChannelHandler;
import cn.t.metric.common.message.response.CmdResponse;
import cn.t.metric.common.repository.SystemInfoRepository;

public class CmdResponseChannelHandler extends AbstractChannelHandler {
    @Override
    public void read(ChannelContext ctx, Object msg) {
        if(msg instanceof CmdResponse) {
            CmdResponse cmdResponse = (CmdResponse)msg;
            System.out.printf("命令响应, 执行状态: %b, 输出: %s%n", cmdResponse.isSuccess(), cmdResponse.getOutput());
        } else {
            ctx.invokeNextChannelRead(msg);
        }
    }
    public CmdResponseChannelHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
