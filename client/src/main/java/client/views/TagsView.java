package client.views;

import client.components.IconButton;
import client.models.BoardModel;
import client.models.TagModel;
import client.utils.AppPalette;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.utils.SmartColor;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class TagsView extends VBox {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text text2;
    @FXML
    private IconButton addButton;
    @FXML
    private IconButton removeButton;

    @AssistedInject
    public TagsView(TagListView.Factory tagListView, @Assisted BoardModel boardModel, AppPalette appPalette) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/components/TagsView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        getStyleClass().add("TagsView");
        TagListView tagListView1 = tagListView.create(boardModel);

        anchorPane.getChildren().addAll(tagListView1);   // Add grid from Example 1-5
        AnchorPane.setBottomAnchor(tagListView1, 30.0);
        AnchorPane.setLeftAnchor(tagListView1, 12.0);
        AnchorPane.setRightAnchor(tagListView1, 12.0);
        AnchorPane.setTopAnchor(tagListView1, 53.0);

        text2.setVisible(tagListView1.getItems().isEmpty());
        tagListView1.getItems().addListener((ListChangeListener<TagModel>) c -> {
            text2.setVisible(tagListView1.getItems().isEmpty());
        });

        addButton.setForegroundColor(appPalette.getDefaultIconForeground());
        addButton.setOnAction(event -> {
            boardModel.createTag("New Tag", new SmartColor(0, 0.8, 0, 1));
        });

        removeButton.setForegroundColor(appPalette.getDefaultIconForeground());
        removeButton.setOnAction(event -> {
            if (tagListView1.getSelectionModel().getSelectedItems().isEmpty()) {
                return;
            }
            var selectedIndex = tagListView1.getSelectionModel();
            boardModel.deleteTag(selectedIndex.getSelectedItem());
        });
    }

    public interface Factory {
        TagsView create(BoardModel boardModel);
    }


}
