package cn.t.metric.client;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.message.request.CmdRequest;
import cn.t.metric.common.message.response.CmdResponse;
import cn.t.metric.common.util.CommandUtil;

public class MessageHandlerAdapter {
    public void handle(ChannelContext channelContext, Object msg) {
        System.out.println("消息: " + msg);
        if (msg instanceof CmdRequest) {
            String output = CommandUtil.execute(((CmdRequest)msg).getCmd());
            CmdResponse cmdResponse = new CmdResponse();
            cmdResponse.setOutput(output);
            channelContext.write(cmdResponse);
        } else {
            System.out.println("no handler found for request: " + msg);
        }
    }
}
