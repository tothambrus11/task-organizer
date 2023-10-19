package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

@SuppressWarnings("unused")
public class RemoveSubtaskMessage extends Message {
    private UUID subtaskId;

    public RemoveSubtaskMessage() {
    }

    public RemoveSubtaskMessage(UUID subtaskId) {
        super();
        this.subtaskId = subtaskId;
    }

    public UUID getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(UUID subtaskId) {
        this.subtaskId = subtaskId;
    }
}
