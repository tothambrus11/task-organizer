package client.views;

import client.models.HighlightModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class HighlightCell extends ListCell<HighlightModel> {
    HighlightCellContent content;

    @AssistedInject
    public HighlightCell(@Assisted ListView<HighlightModel> listView, HighlightCellContent.Factory contentFactory) {
        getStyleClass().add("HighlightCell");
        content = contentFactory.create();

    }

    @Override
    protected void updateItem(HighlightModel item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
            setStyle("-fx-padding: 0px; -fx-pref-height: 0px;");
        } else {
            setStyle("");
            content.setModel(item);
            setGraphic(content);
        }
    }

    public interface Factory {
        HighlightCell create(ListView<HighlightModel> listView);
    }
}
