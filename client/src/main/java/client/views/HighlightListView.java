package client.views;


import client.models.BoardModel;
import client.models.HighlightModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.ListView;

public class HighlightListView extends ListView<HighlightModel> {
    @AssistedInject
    public HighlightListView(@Assisted BoardModel boardModel, HighlightCell.Factory highlightCellFactory) {
        super(boardModel.getHighlightModels());
        getStyleClass().add("HighlightListView");
        setCellFactory(param -> highlightCellFactory.create(this));
    }

    public interface Factory {
        HighlightListView create(BoardModel boardModel);
    }
}
