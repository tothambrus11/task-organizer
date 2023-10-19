package commons.messages.s2c;

import commons.messages.Message;
import commons.models.Task;

public class TaskUpdateS2CMessage extends Message {

    private Task task;

    public TaskUpdateS2CMessage() { /* for object mapper */ }

    public TaskUpdateS2CMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}

