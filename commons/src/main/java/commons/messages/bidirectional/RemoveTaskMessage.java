package commons.messages.bidirectional;

import commons.messages.Message;
import java.util.UUID;

public class RemoveTaskMessage extends Message {

    private UUID taskId;
    private UUID sourceListId;

    @SuppressWarnings("unused")
    public RemoveTaskMessage() {
    }

    public RemoveTaskMessage(UUID taskId, UUID sourceListId) {
        this.taskId = taskId;
        this.sourceListId = sourceListId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getSourceListId() {
        return sourceListId;
    }

    public void setSourceListId(UUID sourceListId) {
        this.sourceListId = sourceListId;
    }
}
