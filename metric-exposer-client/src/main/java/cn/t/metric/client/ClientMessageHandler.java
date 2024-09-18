package cn.t.metric.client;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.ChannelHandler;
import cn.t.metric.common.message.request.CmdRequest;
import cn.t.metric.common.message.response.CmdResponse;
import cn.t.metric.common.util.CommandUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientMessageHandler {

    public static Collection<ChannelHandler> handlerList() {
        List<ChannelHandler> channelHandlerList = new ArrayList<>();
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        channelHandlerList.add(messageHandler.new CmdRequestChannelHandler());
        return channelHandlerList;
    }

    private class CmdRequestChannelHandler implements ChannelHandler {
        @Override
        public void read(ChannelContext channelContext, Object msg) {
            if(msg instanceof CmdRequest) {
                CmdResponse cmdResponse = new CmdResponse();
                try {
                    String output = CommandUtil.execute(((CmdRequest)msg).getCmd());
                    cmdResponse.setSuccess(true);
                    cmdResponse.setOutput(output);
                } catch (Exception e) {
                    cmdResponse.setSuccess(false);
                    cmdResponse.setOutput(ExceptionUtil.getErrorMessage(e));
                }
                channelContext.invokeChannelWrite(cmdResponse);
            } else {
                channelContext.invokeNextChannelRead(msg);
            }
        }
    }
}
