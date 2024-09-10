package cn.t.metric.client;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.MessageHandler;
import cn.t.metric.common.message.request.CmdRequest;
import cn.t.metric.common.message.response.CmdResponse;
import cn.t.metric.common.util.CommandUtil;
import cn.t.metric.common.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientMessageHandler {

    public static Collection<MessageHandler> handlerList() {
        List<MessageHandler> messageHandlerList = new ArrayList<>();
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        messageHandlerList.add(messageHandler.new CmdRequestMessageHandler());
        return messageHandlerList;
    }

    private class CmdRequestMessageHandler implements MessageHandler {
        @Override
        public void handle(ChannelContext channelContext, Object msg) {
            if(msg instanceof CmdRequest) {
                CmdResponse cmdResponse = new CmdResponse();
                try {
                    String output = CommandUtil.execute(((CmdRequest)msg).getCmd());
                    cmdResponse.setOutput(output);
                } catch (Exception e) {
                    cmdResponse.setOutput(ExceptionUtil.getErrorMessage(e));
                }
                channelContext.write(cmdResponse);
            } else {
                channelContext.invokeNextHandlerRead(msg);
            }
        }
    }
}
