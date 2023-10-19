package client.views;

import client.models.TagModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

public class TagHighlightOnCard extends HBox {
    private TagModel tagModel;

    @AssistedInject
    public TagHighlightOnCard(@Assisted TagModel tagModel) {
        this.tagModel = tagModel;
        this.getStyleClass().add("TagHighlightOnCard");
        this.styleProperty().bind(tagModel.colorProperty().map(color -> "-fx-background-color: " + color + ";"));

        var tooltip = new Tooltip();
        tooltip.textProperty().bind(tagModel.nameProperty());
        Tooltip.install(this, tooltip);
    }

    public TagModel getTagModel() {
        return tagModel;
    }

    public interface Factory {
        TagHighlightOnCard create(TagModel tagModel);
    }
}
