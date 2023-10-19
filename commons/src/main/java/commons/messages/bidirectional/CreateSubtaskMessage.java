package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.SubTask;

import java.util.UUID;

@SuppressWarnings("unused")
public class CreateSubtaskMessage extends Message {
    private SubTask createdSubtask;
    private UUID parentTaskId;

    public CreateSubtaskMessage(UUID parentTaskId, SubTask createdSubtask) {
        super();
        this.parentTaskId = parentTaskId;
        this.createdSubtask = createdSubtask;
    }

    public CreateSubtaskMessage() {
    }

    public SubTask getCreatedSubtask() {
        return createdSubtask;
    }

    public void setCreatedSubtask(SubTask createdSubtask) {
        this.createdSubtask = createdSubtask;
    }

    public UUID getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(UUID parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}
