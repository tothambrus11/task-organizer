package client.views;

import client.models.TagModel;
import client.models.TaskModel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class TagsOfTask extends FlowPane {
    private TaskModel taskModel;
    private TagView.Factory tagViewFactory;

    private final ListChangeListener<? super TagModel> onTagsChanged = c -> {
        getChildren().clear();
        for (TagModel tag : taskModel.getTags()) {
            getChildren().add(tagViewFactory.create(tag, taskModel));
        }
        updatePadding();
    };

    @AssistedInject
    public TagsOfTask(@Assisted TaskModel taskModel, TagView.Factory tagViewFactory) {
        super();
        this.taskModel = taskModel;
        this.tagViewFactory = tagViewFactory;
        this.getStyleClass().add("TagsOfTask");

        setVgap(8);
        setHgap(8);
        for (TagModel tag : taskModel.getTags()) {
            getChildren().add(tagViewFactory.create(tag, taskModel));
        }

        updatePadding();

        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
        taskModel.getTags().addListener(onTagsChanged);
    }


    void updatePadding() {
        setPadding(new Insets(0, 0, getChildren().isEmpty() ? 0 : 8, 0));
    }

    public void onDestroy() {
        taskModel.getTags().removeListener(onTagsChanged);
    }

    public interface Factory {
        TagsOfTask create(TaskModel taskModel);
    }
}
