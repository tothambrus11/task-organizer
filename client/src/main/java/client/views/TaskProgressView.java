package client.views;

import client.models.SubTaskModel;
import client.models.TaskModel;
import client.utils.AppPalette;
import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TaskProgressView extends VBox {
    private final HBox progressbarFull;
    private final HBox progressIndicator;
    DoubleProperty progress = new SimpleDoubleProperty(0);
    private Timeline progressAnimationTimeline;

    StringProperty progressText = new SimpleStringProperty("0/1");
    TaskModel taskModel;

    TaskProgressView me = this;
    ChangeListener<Boolean> onCompletedChanged = (observable, oldValue, newValue) -> refreshView();
    ListChangeListener<SubTaskModel> onListChanged = (ListChangeListener.Change<? extends SubTaskModel> c) -> {
        while(c.next()){
            for (var subTaskDummy : c.getRemoved()) {
                subTaskDummy.completedProperty().removeListener(onCompletedChanged);
            }
            for (var subTaskDummy : c.getAddedSubList()) {
                subTaskDummy.completedProperty().addListener(onCompletedChanged);
            }
        }
        refreshView();
    };

    @Inject
    public TaskProgressView(AppPalette appPalette) {
        super();

        progressbarFull = new HBox();
        progressIndicator = new HBox();

        progressIndicator.setStyle("-fx-background-color: #00c200; -fx-background-radius: 4px");
        progressIndicator.setPrefHeight(8);
        progressIndicator.prefWidthProperty().bind(progressbarFull.widthProperty().multiply(progress));

        progressbarFull.getChildren().add(progressIndicator);

        progressbarFull.setStyle("-fx-background-color: #E4E4E4; -fx-background-radius: 4px");

        var progressTextEl = new Text();
        progressTextEl.textProperty().bind(progressText);
        progressTextEl.setTextAlignment(TextAlignment.CENTER);
        progressTextEl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + appPalette.getDefaultIconForeground());

        this.setPadding(new Insets(0, 15, 5, 15));
        getChildren().addAll(progressbarFull, progressTextEl);
        setAlignment(Pos.CENTER);
        setSpacing(2);

    }
    private void animateProgressTo(double newValue) {
        if (progressAnimationTimeline != null) {
            progressAnimationTimeline.stop();
        }

        progressAnimationTimeline = new Timeline(
                new KeyFrame(
                        Duration.millis(200),
                        new KeyValue(progress, newValue)
                )
        );
        progressAnimationTimeline.play();
    }
    void refreshView() {
        int completed = 0;
        int total = taskModel.getSubtasks().size();
        for (var subTaskDummy : taskModel.getSubtasks()) {
            if (subTaskDummy.isCompleted()) {
                completed++;
            }
        }
        if (total == 0) {
            me.setVisible(false);
            me.setManaged(false);
            return;
        }
        me.setVisible(true);
        me.setManaged(true);

        animateProgressTo((double) completed / total);
        progressText.set(completed + "/" + total);
    }

    public void setTask(TaskModel taskModel) {
        if (this.taskModel != null) {
            this.taskModel.getSubtasks().forEach(subTaskModel -> subTaskModel.completedProperty().removeListener(onCompletedChanged));
            this.taskModel.getSubtasks().removeListener(onListChanged);
        }
        this.taskModel = taskModel;

        this.taskModel.getSubtasks().addListener(onListChanged);
        this.taskModel.getSubtasks().forEach(subTaskModel -> subTaskModel.completedProperty().addListener(onCompletedChanged));
        refreshView();
    }

    public interface Factory {
        TaskProgressView create();
    }
}
