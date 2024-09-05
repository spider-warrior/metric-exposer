package cn.t.metric.common.exception;

public class MessageHandlerExecuteException extends RuntimeException {
    public MessageHandlerExecuteException(String message) {
        super(message);
    }
    public MessageHandlerExecuteException(Throwable cause) {
        super(cause);
    }
}
