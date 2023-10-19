package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

public class RemoveBoardMessage extends Message {
    private UUID removedBoardId;

    public RemoveBoardMessage() {
    }
    public RemoveBoardMessage(UUID removedBoardId) {
        this.removedBoardId = removedBoardId;
    }

    public UUID getRemovedBoardId() {
        return removedBoardId;
    }

    public void setRemovedBoardId(UUID removedBoardId) {
        this.removedBoardId = removedBoardId;
    }
}
