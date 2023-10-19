package commons.messages.c2s;

import commons.messages.Message;
import commons.models.Task;

public class TaskAddC2SMessage extends Message {

    private Task task;

    public TaskAddC2SMessage() { /* for object mapper */ }

    public TaskAddC2SMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
