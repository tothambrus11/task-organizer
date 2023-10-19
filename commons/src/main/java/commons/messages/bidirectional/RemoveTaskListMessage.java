package commons.messages.bidirectional;

import commons.messages.Message;
import java.util.UUID;

public class RemoveTaskListMessage extends Message {
    private UUID taskListId;

    public RemoveTaskListMessage() { /* for object mapper */ }

    public RemoveTaskListMessage(UUID taskListId) {
        this.taskListId = taskListId;
    }

    public UUID getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(UUID taskListId) {
        this.taskListId = taskListId;
    }
}
