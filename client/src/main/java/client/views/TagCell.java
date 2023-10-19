package client.views;

import client.models.TagModel;
import com.google.inject.Inject;
import javafx.scene.control.ListCell;

public class TagCell extends ListCell<TagModel> {
    private final TagCellContent content;

    @Inject
    public TagCell(TagCellContent.Factory tagCellContentFactory) {
        super();
        this.content = tagCellContentFactory.create();
        getStyleClass().add("TagCell");
    }

    protected void updateItem(TagModel model, boolean empty) {
        super.updateItem(model, empty);
        if (empty || model == null) {
            setGraphic(null);
            // removing padding
            setStyle("-fx-padding: 0px; -fx-pref-height: 0px;");
        } else {
            setStyle("");
            content.setModel(model);
            setGraphic(content);
        }
    }

    public interface Factory {
        TagCell create();
    }
}
