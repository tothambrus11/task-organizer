package commons.messages.c2s;

import commons.messages.Message;

public class JoinC2SMessage extends Message {
    private String username;
    private String boardKey;

    @SuppressWarnings("unused")
    public JoinC2SMessage() { /* for object mapper */ }

    public JoinC2SMessage(String username, String boardKey) {
        this.username = username;
        this.boardKey = boardKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBoardKey() {
        return boardKey;
    }

    public void setBoardKey(String boardKey) {
        this.boardKey = boardKey;
    }
}
