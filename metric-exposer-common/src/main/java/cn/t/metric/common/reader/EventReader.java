package cn.t.metric.common.reader;

import java.nio.channels.SelectionKey;

@FunctionalInterface
public interface EventReader {
    Object read(SelectionKey key) throws Exception;
}
