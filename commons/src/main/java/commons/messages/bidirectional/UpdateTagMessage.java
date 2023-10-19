package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.Tag;

public class UpdateTagMessage extends Message {
    private Tag tag;

    public UpdateTagMessage() {
    }

    public UpdateTagMessage(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
