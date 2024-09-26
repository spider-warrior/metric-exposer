package cn.t.metric.common.channel;

import cn.t.metric.common.exception.PromiseNotifyException;

public class Promise<V> {
    private volatile Boolean status;
    private volatile V v;
    private volatile PromiseListenerNode<?> lastLoopNode;
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
                current.setExecuted(true);
                notify(current.getListener());
                PromiseListenerNode<V> next = current.getNext();
                if(next == null) {
                    lastLoopNode = current;
                    break;
                }
                current = next;
            }
        }
    }
    public void addListener(PromiseListener<V> listener) {
        if(status == null) {
            PromiseListenerNode<V> newNode = new PromiseListenerNode<>(listener);
            if(this.firstNode == null) {
                this.firstNode = newNode;
                this.currentNode = newNode;
            } else {
                this.currentNode.setNext(newNode);
                this.currentNode = newNode;
            }
            //二次检查发送状态, 如果此时已有响应结果有可能遗漏当前listener执行
            if(status != null) {
                //lastLoopNode不为空说明已循环通知完毕
                if(lastLoopNode != null) {
                    //此时检查newNode状态，如果未执行则立即执行（lastLoopNode晚于node.setExecuted(true)执行）
                    if(!newNode.isExecuted()) {
                        notify(listener);
                    }
                }
            }
        } else {
            notify(listener);
        }
    }

    private void notify(PromiseListener<V> listener) {
        if(status == Boolean.TRUE) {
            listener.success(v);
        } else if(status == Boolean.FALSE) {
            listener.failure(throwable);
        } else {
            throw new PromiseNotifyException("should never go here");
        }
    }
}
