package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.Task;

public class AddTaskMessage extends Message {

    private Task task;

    public AddTaskMessage() { /* for object mapper */ }

    public AddTaskMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
