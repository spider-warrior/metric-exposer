package cn.t.metric.common.channel;

import cn.t.metric.common.exception.PromiseNotifyException;

public class Promise<V> {
    private volatile Boolean status;
    private volatile V v;
    private volatile Throwable throwable;
    private volatile PromiseListenerNode<V> firstNode;
    private volatile PromiseListenerNode<V> currentNode;
    public void success(V v) {
        this.v = v;
        this.status = Boolean.TRUE;
        loopNotify();
    }
    public void failure(Throwable throwable) {
        this.throwable = throwable;
        this.status = Boolean.FALSE;
        loopNotify();
    }
    private void loopNotify() {
        if(firstNode != null) {
            PromiseListenerNode<V> current = firstNode;
            while(true) {
                if(current.tryConsume()) {
                    notify(current.getListener());
                    PromiseListenerNode<V> next = current.getNext();
                    if(next == null) {
                        break;
                    }
                    current = next;
                }
            }
        }
    }
    public void addListener(PromiseListener<V> listener) {
        if(status == null) {
            PromiseListenerNode<V> newNode = new PromiseListenerNode<>(listener);
            if(this.firstNode == null) {
                this.firstNode = newNode;
            } else {
                this.currentNode.setNext(newNode);
            }
            this.currentNode = newNode;
            //二次检查发送状态, 如果此时已有响应结果有可能遗漏当前listener执行
            if(status != null) {
                //争抢式执行
                if(newNode.tryConsume()) {
                    notify(newNode.getListener());
                }
            }
        } else {
            System.out.printf("[%s]addListener: 立即执行%n", Thread.currentThread().getName());
            notify(listener);
        }
    }

    private void notify(PromiseListener<V> listener) {
        if(status == Boolean.TRUE) {
            listener.operationComplete(new PromiseFuture<>(v));
        } else if(status == Boolean.FALSE) {
            listener.operationComplete(new PromiseFuture<>(throwable));
        } else {
            throw new PromiseNotifyException("should never go here");
        }
    }
}
