package commons.messages.s2c;

import commons.messages.Message;
import commons.models.Board;

public class InitBoardStateS2CMessage extends Message {

    Board board;

    @SuppressWarnings("unused")
    private InitBoardStateS2CMessage() { /* for object mapper */ }

    public InitBoardStateS2CMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
