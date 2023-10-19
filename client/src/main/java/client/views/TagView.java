package client.views;

import client.models.TagModel;
import client.models.TaskModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class TagView extends HBox {
    private TagModel tagModel;
    private Text text;

    @AssistedInject
    public TagView(@Assisted TagModel tagModel, @Assisted TaskModel taskModel) {
        this.tagModel = tagModel;
        this.getStyleClass().add("TagView");
        this.styleProperty().bind(tagModel.colorProperty().map(color -> "-fx-background-color: " + color + ";"));

        var tooltip = new Tooltip();
        tooltip.textProperty().bind(tagModel.nameProperty().map(name -> "Remove " + name));
        Tooltip.install(this, tooltip);


        text = new Text();
        text.getStyleClass().add("text");
        text.textProperty().bind(tagModel.nameProperty());

        var removeButton = new ImageView("/client/icons/remove_tag.png");
        removeButton.getStyleClass().add("removeButton");
        removeButton.setFitWidth(21);
        removeButton.setFitHeight(21);

        getChildren().addAll(text, removeButton);

        // todo convert to a button and make it focusable
        setOnMouseClicked(event -> {
            taskModel.removeTag(tagModel);
        });
    }

    public TagModel getTagModel() {
        return tagModel;
    }

    public interface Factory {
        TagView create(TagModel tagModel, TaskModel taskModel);
    }
}
