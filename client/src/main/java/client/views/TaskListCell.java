package client.views;

import client.components.NonRecyclingListView;
import client.models.TaskListModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.scene.layout.VBox;

public class TaskListCell extends VBox implements NonRecyclingListView.ListCell<TaskListModel> {
    private final TaskListCellContent content;
    private final TaskListModel taskListModel;

    @Inject
    public TaskListCell(@Assisted TaskListModel taskListModel, TaskListCellContent.Factory taskListCellContentFactory) {
        this.taskListModel = taskListModel;
        this.content = taskListCellContentFactory.create(taskListModel);
        this.getChildren().add(content);
        this.content.prefHeightProperty().bind(heightProperty());
    }

    @Override
    public TaskListModel getModelItem() {
        return taskListModel;
    }

    @Override
    public void onRemoved() {
        this.content.onRemoved();
    }

    public interface Factory {
        TaskListCell create(TaskListModel taskListModel);
    }

    public TaskListCellContent getContent() {
        return content;
    }
}
