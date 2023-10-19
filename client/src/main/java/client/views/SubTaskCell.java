package client.views;

import client.components.SubTaskCellContent;
import client.models.SubTaskModel;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.ListCell;

public class SubTaskCell extends ListCell<SubTaskModel> {

    private SubTaskCellContent content;

    @AssistedInject
    public SubTaskCell(SubTaskCellContent.Factory subTaskCellContentFactory) {
        super();
        getStyleClass().add("SubTaskCell");
        content = subTaskCellContentFactory.create();
    }

    @Override
    protected void updateItem(SubTaskModel item, boolean empty) {
        super.updateItem(item, empty);
        if(item != null) {
            setStyle("");
            content.setModel(item);
            setGraphic(content);
        } else {
            setGraphic(null);
            setStyle("-fx-padding: 0px; -fx-pref-height: 0px; -fx-min-height: 0px; -fx-max-height: 0px;");
        }
    }

    public interface Factory {
        SubTaskCell create();
    }
}
