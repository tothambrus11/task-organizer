package commons.messages.s2c;

import commons.messages.Message;

public class QuitS2CMessage extends Message {

    private long userId;

    @SuppressWarnings("unused")
    private QuitS2CMessage() { /* for object mapper */ }

    public QuitS2CMessage(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
