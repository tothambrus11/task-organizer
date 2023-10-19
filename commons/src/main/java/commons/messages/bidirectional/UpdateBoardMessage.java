package commons.messages.bidirectional;

import commons.messages.Message;
import commons.models.Board;

public class UpdateBoardMessage extends Message {
    private Board board;

    public UpdateBoardMessage() { /* for object mapper */ }

    public UpdateBoardMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
