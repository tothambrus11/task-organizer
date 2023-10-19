package client.components;

import client.SimpleFXMLLoader;
import client.models.SubTaskModel;
import client.utils.AppPalette;
import com.google.inject.assistedinject.AssistedInject;
import commons.utils.SmartColor;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class SubTaskCellContent extends HBox {

    @FXML
    private HBox hBox;
    @FXML
    private IconButton deleteButton;
    @FXML
    private VBox vBox;
    @FXML
    private IconButton upButton;
    @FXML
    private IconButton downButton;
    @FXML
    private TextInput titleField;
    private final ChangeListener<String> onModelNameChanged = (observable, oldValue, newValue) -> {
        titleField.setText(newValue);
    };
    @FXML
    private Checkbox completedCheck;
    private final ChangeListener<Boolean> onModelCompletedChanged = (observable, oldValue, newValue) -> {
        completedCheck.checkedProperty().setValue(newValue);
    };
    private SubTaskModel subTaskModel;

    @AssistedInject
    public SubTaskCellContent(AppPalette appPalette, SimpleFXMLLoader fxmlLoader) {
        fxmlLoader.initView("/client/components/SubtaskCellContent.fxml", this);
        getStyleClass().add("SubTaskCellContent");

        titleField.focusedProperty().addListener((observable, oldValue, focused) -> {
            if (focused) {
                System.out.println("focused");
                getStyleClass().add("focusedRow");
            } else {
                System.out.println("unfocused");
                getStyleClass().remove("focusedRow");
            }
        });

        upButton.setForegroundColor(appPalette.getDefaultIconForeground());
        upButton.setBackgroundColor(new SmartColor(1, 1, 1, 1));
        downButton.setForegroundColor(appPalette.getDefaultIconForeground());
        downButton.setBackgroundColor(new SmartColor(1, 1, 1, 1));
        deleteButton.setForegroundColor(appPalette.getDefaultIconForeground());
        deleteButton.setBackgroundColor(new SmartColor(1, 1, 1, 1));

        upButton.setOnAction(event -> subTaskModel.moveUp());
        downButton.setOnAction(event -> subTaskModel.moveDown());
        deleteButton.setOnAction(event -> subTaskModel.delete());


        completedCheck.checkedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(subTaskModel.getName() + " - " + oldValue + " - " + newValue);
            if (oldValue != newValue) {
                subTaskModel.setCompleted(newValue);
                System.out.println(subTaskModel.isCompleted());
                subTaskModel.save();
            }
        });

        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                subTaskModel.setName(titleField.getText());
                subTaskModel.save();
            }
        });
    }

    public void clearBindings() {
        if (subTaskModel == null) return;
        subTaskModel.nameProperty().removeListener(onModelNameChanged);
        subTaskModel.completedProperty().removeListener(onModelCompletedChanged);
    }

    public void setModel(SubTaskModel subtask) {
        clearBindings();
        this.subTaskModel = subtask;

        titleField.textProperty().setValue(subTaskModel.getName());
        completedCheck.checkedProperty().setValue(subTaskModel.isCompleted());

        subTaskModel.nameProperty().addListener(onModelNameChanged);
        subTaskModel.completedProperty().addListener(onModelCompletedChanged);
    }

    public interface Factory {
        SubTaskCellContent create();
    }
}
