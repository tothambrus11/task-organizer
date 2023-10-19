package commons.messages.bidirectional;

import commons.messages.Message;
import java.util.UUID;

@SuppressWarnings("unused")
public class RemoveTagFromTaskMessage extends Message {
    private UUID taskId;
    private UUID tagId;

    public RemoveTagFromTaskMessage() {
    }

    public RemoveTagFromTaskMessage(UUID taskId, UUID tagId) {
        super();
        this.taskId = taskId;
        this.tagId = tagId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }
}
