package cn.t.metric.server.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.handler.TypeMatchedChannelHandler;
import cn.t.metric.common.message.response.CmdResponse;

public class CmdResponseChannelHandler extends TypeMatchedChannelHandler<CmdResponse> {

    @Override
    public void doRead(ChannelContext ctx, CmdResponse msg) throws Exception {
        System.out.printf("命令响应, 执行状态: %b, 输出: %s%n", msg.isSuccess(), msg.getOutput());
    }

}
