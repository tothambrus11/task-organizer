package client.views;

import client.models.TagModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class TagHighlightListOnCard extends FlowPane {
    private ObservableList<TagModel> tags;

    private TagHighlightOnCard.Factory tagHighlightOnCardFactory;
    private final ListChangeListener<? super TagModel> onTagsChanged = c -> {
        refreshList();
    };

    @AssistedInject
    public TagHighlightListOnCard(@Assisted ObservableList<TagModel> tags, TagHighlightOnCard.Factory tagHighlightOnCardFactory) {
        super();
        this.tags = tags;
        this.tagHighlightOnCardFactory = tagHighlightOnCardFactory;
        this.getStyleClass().add("TagHighlightListOnCard");

        setVgap(8);
        setHgap(8);


        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
        tags.addListener(onTagsChanged);

        refreshList();
    }

    void refreshList() {
        getChildren().clear();
        for (TagModel tag : tags) {
            getChildren().add(tagHighlightOnCardFactory.create(tag));
        }
        updatePadding();
    }

    void updatePadding() {
        setPadding(new Insets(0, 15, getChildren().isEmpty() ? 0 : 15, 15));
    }

    public void onDestroy() {
        tags.removeListener(onTagsChanged);
    }

    public interface Factory {
        TagHighlightListOnCard create(ObservableList<TagModel> tags);
    }
}
