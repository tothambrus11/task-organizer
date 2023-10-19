package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

@SuppressWarnings("unused")
public class AddTagToTaskMessage extends Message {
    private UUID taskId;

    private UUID tagId;

    public AddTagToTaskMessage() {
    }

    public AddTagToTaskMessage(UUID taskId, UUID tagId) {
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
