package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

@SuppressWarnings("unused")
public class RemoveHighlightMessage extends Message {
    private UUID removedHighlightId;

    public RemoveHighlightMessage(UUID removedHighlightId) {
        this.removedHighlightId = removedHighlightId;
    }

    public RemoveHighlightMessage() {
    }

    public UUID getRemovedHighlightId() {
        return removedHighlightId;
    }

    public void setRemovedHighlightId(UUID removedHighlightId) {
        this.removedHighlightId = removedHighlightId;
    }
}
