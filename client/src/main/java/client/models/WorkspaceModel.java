package client.models;

import client.contexts.UserContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;


public class WorkspaceModel {
    private final ObservableList<BoardPreviewModel> boardPreviews = FXCollections.observableArrayList();
    private final SortedList<BoardPreviewModel> sortedBoardPreviews;

    private UserContext userContext;

    public WorkspaceModel(UserContext userContext) {
        this.userContext = userContext;

        sortedBoardPreviews = new SortedList<>(boardPreviews, (o1, o2) -> {
            if (o1.getLastAccessTime() == null && o2.getLastAccessTime() == null) return 0;
            else if (o1.getLastAccessTime() == null) return 1;
            else if (o2.getLastAccessTime() == null) return -1;
            else return o2.getLastAccessTime().compareTo(o1.getLastAccessTime());
        });
    }

    public SortedList<BoardPreviewModel> getSortedBoardPreviews() {
        return sortedBoardPreviews;
    }

    public void addBoardPreview(BoardPreviewModel boardPreview) {
        boardPreviews.add(boardPreview);
    }


    public void deleteBoardPreview(BoardPreviewModel boardPreviewModel) {
        boardPreviews.remove(boardPreviewModel);
        userContext.getKeychain().removeEntry(boardPreviewModel.getJoinKey());
        userContext.deleteBoard(boardPreviewModel.getId());
    }

    public void leaveBoardPreview(BoardPreviewModel boardPreviewModel) {
        boardPreviews.remove(boardPreviewModel);
        userContext.getKeychain().removeEntry(boardPreviewModel.getJoinKey());
    }

    public void clear() {
        boardPreviews.clear();
    }
}