package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.TaskList;

public class AddTaskListMessage extends Message {
    private TaskList taskList;

    public AddTaskListMessage() { /* for object mapper */ }

    public AddTaskListMessage(TaskList taskList) {
        this.taskList = taskList;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }
}
