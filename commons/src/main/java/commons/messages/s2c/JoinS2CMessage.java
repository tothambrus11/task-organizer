package commons.messages.s2c;

import commons.messages.Message;
import commons.models.User;

public class JoinS2CMessage extends Message {

    private User user;

    @SuppressWarnings("unused")
    private JoinS2CMessage() { /* for object mapper */ }

    public JoinS2CMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
