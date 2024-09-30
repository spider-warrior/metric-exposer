package cn.t.metric.common.channel;

import cn.t.metric.common.eventloop.SingleThreadEventLoop;
import cn.t.metric.common.exception.PromiseNotifyException;

public class Promise<V> {

    private final SingleThreadEventLoop eventLoop;
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
    public Promise<V> addListener(PromiseListener<V> listener) {
        this.eventLoop.addTask(() -> {
            if(status == null) {
                PromiseListenerNode<V> newNode = new PromiseListenerNode<>(listener);
                if(this.firstNode == null) {
                    this.firstNode = newNode;
                } else {
                    this.currentNode.setNext(newNode);
                }
                this.currentNode = newNode;
            } else {
                notify(listener);
            }
            return null;
        }, 0);
        return this;
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

    public Promise(SingleThreadEventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }
}
