package commons.messages.s2c;

import commons.messages.Message;
import commons.models.Task;

public class TaskAddS2CMessage extends Message {

    private Task task;

    public TaskAddS2CMessage() { /* for object mapper */ }

    public TaskAddS2CMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
