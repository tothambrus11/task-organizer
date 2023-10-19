package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.TaskHighlight;

@SuppressWarnings("unused")
public class CreateHighlightMessage extends Message {
    private TaskHighlight highlight;

    public CreateHighlightMessage(TaskHighlight highlight) {
        this.highlight = highlight;
    }

    public CreateHighlightMessage() {
    }

    public TaskHighlight getHighlight() {
        return highlight;
    }

    public void setHighlight(TaskHighlight highlight) {
        this.highlight = highlight;
    }
}
