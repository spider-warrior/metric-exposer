package cn.t.metric.common.handler;

import cn.t.metric.common.channel.ChannelContext;
import cn.t.metric.common.util.TypeParameterMatcher;

public abstract class TypeMatchedChannelHandler<I> implements ChannelHandler {

    private final TypeParameterMatcher matcher = new TypeParameterMatcher.ReflectiveMatcher(TypeParameterMatcher.find(this, TypeMatchedChannelHandler.class, "I"));

    @Override
    public final void read(ChannelContext ctx, Object msg) throws Exception {
        if(matcher.match(msg)) {
            @SuppressWarnings("unchecked")
            I castedMsg = (I) msg;
            doRead(castedMsg);
        } else {
            ctx.invokeNextChannelRead(msg);
        }
    }
    public abstract void doRead(I msg) throws Exception;
}
