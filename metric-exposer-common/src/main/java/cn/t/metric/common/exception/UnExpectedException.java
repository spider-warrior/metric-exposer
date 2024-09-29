package cn.t.metric.common.exception;

public class UnExpectedException extends RuntimeException {
    public UnExpectedException(String message) {
        super("should never go here: " + message);
    }
}
