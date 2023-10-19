package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.SubTask;

@SuppressWarnings("unused")
public class UpdateSubtaskMessage extends Message {
    private SubTask subtask;

    public UpdateSubtaskMessage() {
    }

    public UpdateSubtaskMessage(SubTask subtask) {
        super();
        this.subtask = subtask;
    }

    public SubTask getSubtask() {
        return subtask;
    }

    public void setSubtask(SubTask subTask) {
        this.subtask = subTask;
    }
}
