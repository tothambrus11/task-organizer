package client.components;

import client.models.SubTaskModel;
import client.models.TaskModel;
import client.views.SubTaskCell;
import com.google.inject.assistedinject.AssistedInject;
import javafx.scene.control.ListView;

public class SubTaskList extends ListView<SubTaskModel> {

    private TaskModel taskModel;

    @AssistedInject
    public SubTaskList(SubTaskCell.Factory subTaskCellFactory) {
        getStyleClass().add("SubTaskList");
        setFocusTraversable(false);
        setCellFactory(param -> subTaskCellFactory.create());

        setFixedCellSize(45);

    }

    public interface Factory {
        SubTaskList create();
    }

    public void setModel(TaskModel taskModel) {
        this.taskModel = taskModel;
        this.setItems(taskModel.getSubtasks());
    }

//    public void moveSubTaskUp(SubTaskCellContent subTaskCellContent) {
//        int tempIndex = this.container.getChildren().indexOf(subTaskCellContent);
//        if (tempIndex > 0) {
//            swapSubTasks(tempIndex, tempIndex - 1);
//        }
//    }
//
//    public void moveSubTaskDown(SubTaskCellContent subTaskCellContent) {
//        int tempIndex = this.container.getChildren().indexOf(subTaskCellContent);
//        if (tempIndex < this.container.getChildren().size() - 1) {
//            swapSubTasks(tempIndex, tempIndex + 1);
//        }
//    }
//
//    public void swapSubTasks(int index1, int index2) {
//        SubTaskCellContent tempSubTaskCellContent1 = (SubTaskCellContent) this.container.getChildren().get(index1);
//        SubTaskCellContent tempSubTaskCellContent2 = (SubTaskCellContent) this.container.getChildren().get(index2);
//        this.container.getChildren().set(index2, dummySubTaskCellContent);
//        this.container.getChildren().set(index1, tempSubTaskCellContent2);
//
//        this.container.getChildren().set(index2, tempSubTaskCellContent1);
//    }
   // public int getSubTaskAmount() { return this.container.getChildren().size(); }
}