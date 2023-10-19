package client.views;

import client.models.TaskListModel;
import client.models.TaskModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;

public class TaskListView extends ListView<TaskModel> {

    BooleanProperty scrollbarVisible = new SimpleBooleanProperty();

    public boolean isScrollbarVisible() {
        return scrollbarVisible.get();
    }

    public BooleanProperty scrollbarVisibleProperty() {
        return scrollbarVisible;
    }

    @Inject
    public TaskListView(@Assisted TaskListModel taskListModel, TaskCell.Factory taskCellFactory) {
        super(taskListModel.getSortedTasks());
        getStyleClass().add("TaskList");

        setCellFactory(param -> taskCellFactory.create(this, taskListModel));
        setFocusTraversable(false);

        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                ScrollBar verticalScrollbar = (ScrollBar) lookup(".scroll-bar:vertical");
                scrollbarVisible.bind(verticalScrollbar.visibleProperty());
            }
        });

        scrollbarVisible.addListener(e -> {
            Platform.runLater(this::updateAllCellPaddings);
        });
    }


    private void updateAllCellPaddings() {
        lookupAll(".list-cell").forEach(cell -> {
            ((TaskCell) cell).updateCellPaddings(scrollbarVisible.get());
        });
    }

    public interface Factory {
        TaskListView create(TaskListModel taskListModel);
    }
}