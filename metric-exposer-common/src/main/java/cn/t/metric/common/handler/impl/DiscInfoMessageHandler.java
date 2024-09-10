package cn.t.metric.common.handler.impl;

import cn.t.metric.common.context.ChannelContext;
import cn.t.metric.common.handler.AbstractMessageHandler;
import cn.t.metric.common.message.infos.DiscInfo;
import cn.t.metric.common.message.infos.SystemInfo;
import cn.t.metric.common.repository.SystemInfoRepository;
import cn.t.metric.common.util.PopulateUtil;

import java.util.ArrayList;
import java.util.List;

public class DiscInfoMessageHandler extends AbstractMessageHandler {

    @Override
    public void handle(ChannelContext channelContext, Object msg) {
        if(msg instanceof DiscInfo) {
            SystemInfo systemInfo = systemInfoRepository.queryByIp(channelContext.getRemoteIp());
            if(systemInfo == null) {
                systemInfo = new SystemInfo();
                List<DiscInfo> discInfoList = new ArrayList<>();
                discInfoList.add((DiscInfo)msg);
                systemInfo.setDiscInfoList(discInfoList);
                systemInfoRepository.save(channelContext.getRemoteIp(), systemInfo);
            } else {
                PopulateUtil.populateDiscInfo(systemInfo, (DiscInfo)msg);
            }
        } else {
            channelContext.invokeNextHandlerRead(msg);
        }
    }

    public DiscInfoMessageHandler(SystemInfoRepository systemInfoRepository) {
        super(systemInfoRepository);
    }
}
