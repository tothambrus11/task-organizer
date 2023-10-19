package commons.messages.c2s;

import commons.messages.Message;

public class QuitC2SMessage extends Message {

    private String dummy  = "";

    @SuppressWarnings("unused")
    public QuitC2SMessage() { /* for object mapper */ }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }
}
