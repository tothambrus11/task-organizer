package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.TaskList;

public class UpdateTaskListMessage extends Message {
    TaskList taskList;

    public UpdateTaskListMessage() { /* for object mapper */ }

    public UpdateTaskListMessage(TaskList taskList) {
        this.taskList = taskList;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

}
