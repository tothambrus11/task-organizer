package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.Task;

public class UpdateTaskMessage extends Message {

    private Task task;

    public UpdateTaskMessage() { /* for object mapper */ }

    public UpdateTaskMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
