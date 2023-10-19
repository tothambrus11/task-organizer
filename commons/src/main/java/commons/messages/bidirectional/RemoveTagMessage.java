package commons.messages.bidirectional;

import commons.messages.Message;

import java.util.UUID;

public class RemoveTagMessage extends Message {
    private UUID removedTagId;

    public RemoveTagMessage(){
    }
    public RemoveTagMessage(UUID removedTagId) {
        this.removedTagId = removedTagId;
    }

    public UUID getRemovedTagId() {
        return removedTagId;
    }

    public void setRemovedTagId(UUID removedTagId) {
        this.removedTagId = removedTagId;
    }
}
