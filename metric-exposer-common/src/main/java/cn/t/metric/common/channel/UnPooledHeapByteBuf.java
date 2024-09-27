package cn.t.metric.common.channel;

public class UnPooledHeapByteBuf {
    private static final int minExpandEachTimeSize = 1024;
    private static final int maxExpandEachTimeSize = 1024 * 1024;

    private byte[] buf;
    //readerIndex <= writerIndex < buf.length
    private int readerIndex = 0;
    private int writerIndex = 0;
    private int lastExpandSize = 0;

    public int writerIndex() {
        return writerIndex;
    }

    public int readerIndex() {
        return readerIndex;
    }

    final void ensureWritable(int writeBytes) {
        //剩余空间
        int remainWritableBytes = buf.length - writerIndex;
        if(writeBytes > remainWritableBytes) {
            //最大可用空间
            int maxRemainWritableBytes = remainWritableBytes + readerIndex;
            //如果压缩后空间可容纳写入内容
            if(maxRemainWritableBytes >= writeBytes) {
                compact(readerIndex, writerIndex - readerIndex);
            } else {
                //扩展字段
                int expandSize = writeBytes - maxRemainWritableBytes;
                if(expandSize < maxExpandEachTimeSize) {
                    //最小扩容检查
                    if(expandSize < minExpandEachTimeSize) {
                        expandSize = minExpandEachTimeSize;
                    } else {
                        expandSize = minExpandEachTimeSize * 2;
                    }
                    //保证扩容大小是单调递增
                    if(expandSize < lastExpandSize) {
                        expandSize = lastExpandSize;
                    }
                }
                int newCapacity = buf.length + expandSize;
                byte[] newBuf = new byte[newCapacity];
                System.arraycopy(buf, readerIndex, newBuf, 0, writerIndex - readerIndex);
                buf = newBuf;
            }
        }
    }

    private void compact(int startIndex, int count) {
        for (int i = startIndex; i < startIndex + count; i++) {
            buf[i-startIndex] = buf[i];
        }
        //clear(有writerIndex做边界限制，可以不用清理)
//        for (int i = count; i < startIndex + count; i++) {
//            buf[i] = 0;
//        }
    }
}
