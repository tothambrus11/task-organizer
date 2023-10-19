package client.views;

import client.models.TaskListModel;
import client.models.TaskModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;

public class TaskCell extends ListCell<TaskModel> {
    private final TaskCellContent content;
    private final TaskListModel taskListModel;
    private final TaskListView listView;
    private final BooleanProperty isValid = new SimpleBooleanProperty(false);

    @Inject
    public TaskCell(@Assisted TaskListView listView, @Assisted TaskListModel taskListModel, TaskCellContent.Factory taskCellContentFactory) {
        super();
        this.taskListModel = taskListModel;
        this.listView = listView;
        this.content = taskCellContentFactory.create(taskListModel);
        getStyleClass().add("TaskCell");

        this.maxWidthProperty().bind(listView.widthProperty());
        this.content.prefWidthProperty().bind(listView.prefWidthProperty().subtract(30));
        this.content.maxWidthProperty().bind(listView.widthProperty().subtract(30));

        taskListModel.dragoverPosProperty().addListener(this::onDragoverPosChange);

    }

    @Override
    protected void updateItem(TaskModel task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null || task.isDummy()) {
            isValid.set(false);
            setGraphic(null);
            setPrefHeight(0); // Set the preferred height to 0 for dummy tasks
            setMaxHeight(0); // Set the max height to 0 for dummy tasks
            setStyle("-fx-pref-height: 0px");
        } else {
            isValid.set(true);
            setStyle("");
            setPrefHeight(USE_COMPUTED_SIZE);
            setMaxHeight(USE_COMPUTED_SIZE);

            content.setTask(task, getIndex());
            setGraphic(content);
        }

        Platform.runLater(()->updateCellPaddings(listView.isScrollbarVisible()));
    }

    private void onDragoverPosChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (getIndex() == -1 || !isValid.get()) {
            return;
        }
        var listSize = getListView().getItems().size();
        var lastElementIndex = listSize - 2;
        var afterLastElementIndex = listSize - 1;


        if (oldValue.intValue() == getIndex()) {
            content.setShowDragOverTop(false);
        } else if (oldValue.intValue() == afterLastElementIndex && getIndex() == lastElementIndex) {
            content.setShowDragOverBottom(false);
        }


        if (newValue.intValue() == getIndex()) {
            content.setShowDragOverTop(true);
        } else if (newValue.intValue() == afterLastElementIndex && getIndex() == lastElementIndex) {
            content.setShowDragOverBottom(true);
        }
    }

    public void updateCellPaddings(boolean scrollbarVisible) {
        Insets newInsets;
        if (getTask() != null && !getTask().isDummy()) {
            newInsets = scrollbarVisible ? new Insets(2, 2, 2, 15) : new Insets(2, 15, 2, 15);
        } else {
            newInsets = new Insets(0);
        }
        setPadding(newInsets);
        requestLayout();
    }


    public TaskModel getTask() {
        return getItem();
    }

    public interface Factory {
        TaskCell create(TaskListView listView, TaskListModel taskListModel);
    }
}
