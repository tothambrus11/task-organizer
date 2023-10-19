package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

@SuppressWarnings("unused")
public class SwapSubtaskPositionMessage extends Message {
    private UUID subtaskId1;
    private UUID subtaskId2;

    public SwapSubtaskPositionMessage(UUID subtaskId1, UUID subtaskId2) {
        this.subtaskId1 = subtaskId1;
        this.subtaskId2 = subtaskId2;
    }

    public SwapSubtaskPositionMessage() {
    }

    public UUID getSubtaskId1() {
        return subtaskId1;
    }

    public void setSubtaskId1(UUID subtaskId1) {
        this.subtaskId1 = subtaskId1;
    }

    public UUID getSubtaskId2() {
        return subtaskId2;
    }

    public void setSubtaskId2(UUID subtaskId2) {
        this.subtaskId2 = subtaskId2;
    }
}
