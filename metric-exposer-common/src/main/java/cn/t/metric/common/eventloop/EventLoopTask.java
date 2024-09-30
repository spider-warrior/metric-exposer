package cn.t.metric.common.eventloop;

public class EventLoopTask {

    private final Runnable command;
    private final long deadlineMills;

    public Runnable getCommand() {
        return command;
    }

    public long getDeadlineMills() {
        return deadlineMills;
    }

    public EventLoopTask(Runnable command, long deadlineMills) {
        this.command = command;
        this.deadlineMills = deadlineMills;
    }
}
