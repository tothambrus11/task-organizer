package client.views;

import client.models.BoardModel;
import client.models.TagModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.ListView;

public class TagListView extends ListView<TagModel> {
    @AssistedInject
    public TagListView(@Assisted BoardModel boardModel, TagCell.Factory tagCellFactory) {
        super(boardModel.getTagModels());
        setCellFactory(param -> tagCellFactory.create());
        getStyleClass().add("TagList");
    }
    public interface Factory {
        TagListView create(BoardModel boardModel);
    }


}
