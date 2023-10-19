package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.TaskHighlight;

public class UpdateHighlightMessage extends Message {
    private TaskHighlight highlight;

    public UpdateHighlightMessage(TaskHighlight highlight) {
        this.highlight = highlight;
    }

    public UpdateHighlightMessage() {
    }

    public TaskHighlight getHighlight() {
        return highlight;
    }

    public void setHighlight(TaskHighlight highlight) {
        this.highlight = highlight;
    }
}
