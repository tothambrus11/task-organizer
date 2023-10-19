package client.views;

import client.components.BoardPreview;
import client.models.BoardPreviewModel;
import com.google.inject.Inject;
import javafx.scene.control.ListCell;

public class BoardPreviewCell extends ListCell<BoardPreviewModel> {

    private final BoardPreview content;

    @Inject
    public BoardPreviewCell(BoardPreview.Factory boardPreviewFactory) {
        super();
        this.content = boardPreviewFactory.create();
        this.getStyleClass().add("BoardPreviewCell");
    }

    public interface Factory {
        BoardPreviewCell create();
    }

    @Override
    protected void updateItem(BoardPreviewModel model, boolean empty) {
        super.updateItem(model, empty);
        if (empty || model == null) {
            setGraphic(null);
            // removing padding
            setStyle("-fx-padding: 0px; -fx-pref-height: 0px; ");
        } else {
            setStyle("");
            content.setModel(model);
            setGraphic(content);
        }
    }
}