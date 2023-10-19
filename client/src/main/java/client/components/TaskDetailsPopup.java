package client.components;

import client.SimpleFXMLLoader;
import client.models.BoardModel;
import client.models.HighlightModel;
import client.models.TagModel;
import client.models.TaskModel;
import client.utils.AppPalette;
import client.views.TagsOfTask;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import commons.utils.SmartColor;
import javafx.application.Platform;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.stream.Collectors;

public class TaskDetailsPopup extends PopUp {

    private final TagsOfTask.Factory tagsOfTaskFactory;
    private final AppPalette appPalette;
    private final BoardModel boardModel;
    private final SubTaskList.Factory subtaskListFactory;
    @FXML
    VBox content;
    @FXML
    TextInput titleField;
    private final ChangeListener<String> onModelTitleChanged = (observable, oldValue, newValue) -> {
        titleField.setText(newValue);
    };
    @FXML
    TextButton escapeButton;
    @FXML
    TextButton deleteButton;
    @FXML
    TextAreaInput descriptionField;
    private final ChangeListener<String> onModelDescriptionChanged = (observable, oldValue, newValue) -> {
        descriptionField.setText(newValue);
        if (this.descriptionField.isFocused()) this.descriptionField.end();
    };
    @FXML
    ComboBox<String> tagInput;
    @FXML
    IconButton addTaskButton;

    SubTaskList subTaskList;
    private TaskModel taskModel;
    @FXML
    private HBox tagListContainer;

    @FXML
    private HBox subTaskListContainer;
    @FXML
    private ComboBox<HighlightModel> highlightSelection;
    private ChangeListener<? super HighlightModel> onModelHighlightChanged = (observable, oldValue, newValue) -> {
        updateHighlightSelection();
    };
    void updateHighlightSelection() {
        highlightSelection.setValue(taskModel.getHighlight());
    }

    @Inject
    public TaskDetailsPopup(@Assisted BoardModel boardModel,
                            AppPalette appPalette,
                            TagsOfTask.Factory tagsOfTaskFactory,
                            SubTaskList.Factory subTaskListFactory,
                            SimpleFXMLLoader fxmlLoader) {
        this.tagsOfTaskFactory = tagsOfTaskFactory;
        this.appPalette = appPalette;
        this.boardModel = boardModel;
        this.subtaskListFactory = subTaskListFactory;

        fxmlLoader.initView("/client/components/TaskDetailsPopup.fxml", this);

        this.getStyleClass().add("TaskDetailsPopup");

        setupHeader();
        setupBasicFields();
        setupSubtasksSection();
        setupTagInput(boardModel);
        setupHighlightComboBox();
    }

    private void setupHighlightComboBox() {
        highlightSelection.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(HighlightModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    textProperty().unbind();
                    setText("");
                } else {
                    textProperty().bind(item.nameProperty());
                }
            }
        });
        highlightSelection.setButtonCell(highlightSelection.getCellFactory().call(null));


        highlightSelection.setConverter(new StringConverter<>() {
            @Override
            public String toString(HighlightModel object) {
                if (object == null) return "";
                return object.getName();
            }

            @Override
            public HighlightModel fromString(String string) {
                return taskModel.getParentTaskList().getParentBoard().getHighlightModels().stream()
                        .filter(highlightModel -> highlightModel.getName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        highlightSelection.setOnAction(e->{
            if(highlightSelection.getValue() == null) return;

            taskModel.setHighlight(highlightSelection.getValue());
            taskModel.save();
        });

    }

    private void setupHeader() {
        escapeButton.setOnMouseClicked(e -> {
            this.close();
        });

        deleteButton.setOnMouseClicked(e -> {
            this.taskModel.remove();
            this.close();
        });
    }

    private void setupBasicFields() {
        titleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                taskModel.setTitle(titleField.getText());
                taskModel.save();
            }
        });

        descriptionField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                taskModel.setDescription(descriptionField.getText());
                taskModel.save();
            }
        });
    }

    private void setupSubtasksSection() {
        subTaskList = subtaskListFactory.create();
        HBox.setHgrow(subTaskList, Priority.ALWAYS);
        subTaskListContainer.getChildren().add(subTaskList);

        addTaskButton.setBackgroundColor(new SmartColor(1, 1, 1, 1));
        addTaskButton.setForegroundColor(appPalette.getDefaultIconForeground());

        addTaskButton.setOnAction(e -> taskModel.createSubTaskAtTheEnd());
    }

    private void setupTagInput(BoardModel boardModel) {
        ListBinding<String> tagListBinding = new ListBinding<>() {
            {
                super.bind(boardModel.getTagModels());
            }

            @Override
            protected ObservableList<String> computeValue() {
                return boardModel.getTagModels().stream().map(TagModel::getName).collect(Collectors.toCollection(FXCollections::observableArrayList));
            }
        };

        tagInput.setItems(tagListBinding);

        tagInput.setOnAction(e -> {
            if (tagInput.getValue() == null || tagInput.getValue().trim().isEmpty()) return;

            boolean alreadyHasTag = taskModel.getTags().stream().anyMatch(tagModel -> tagModel.getName().equals(tagInput.getValue()));
            if (alreadyHasTag) {
                System.out.println("Task already has this tag");
                Platform.runLater(() -> tagInput.setValue(""));
                return;
            }
            var matchingTag = boardModel.getTagModels().stream().filter(tagModel -> tagModel.getName().equals(tagInput.getValue())).findFirst();
            if (matchingTag.isPresent()) {
                System.out.println("Tag already exists, adding it to task");
                Platform.runLater(() -> {
                    tagInput.setValue("");
                    taskModel.addTag(matchingTag.get());
                });
            } else {
                var createdTag = boardModel.createTag(tagInput.getValue(), SmartColor.valueOf("hsl(" + Math.round(Math.random() * 360) + ",90%,80%)"));
                tagInput.setDisable(true);

                new Thread(() -> {
                    try {
                        Thread.sleep(40);
                        Platform.runLater(() -> tagInput.requestFocus());

                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            tagInput.setDisable(false);
                            tagInput.setValue("");
                            taskModel.addTag(createdTag);
                            System.out.println("Tag created and added to task" + createdTag.getName());
                            tagInput.requestFocus();
                        });
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }).start();
            }
        });
    }

    public void setModel(TaskModel taskModel) {
        this.taskModel = taskModel;
        titleField.textProperty().setValue(taskModel.titleProperty().getValue());
        descriptionField.textProperty().setValue(taskModel.descriptionProperty().getValue());
        refreshTagList();
        subTaskList.setModel(taskModel);

        highlightSelection.setItems(taskModel.getParentTaskList().getParentBoard().getHighlightModels());
        taskModel.highlightProperty().addListener(onModelHighlightChanged);
        updateHighlightSelection();

        taskModel.setOnDelete(this::close);
        taskModel.titleProperty().addListener(onModelTitleChanged);
        taskModel.descriptionProperty().addListener(onModelDescriptionChanged);
    }

    private void refreshTagList() {
        if (!tagListContainer.getChildren().isEmpty()) {
            var tagList = (TagsOfTask) tagListContainer.getChildren().get(0);
            tagList.onDestroy();
            tagListContainer.getChildren().clear();
        }
        tagListContainer.getChildren().add(tagsOfTaskFactory.create(taskModel));
    }

    public void open(TaskModel taskModel) {
        setModel(taskModel);
        super.open();
    }

    @Override
    public void close() {
        super.close();
        clearBindings();
    }

    private void clearBindings() {
        taskModel.setOnDelete(() -> {
        });
        taskModel.titleProperty().removeListener(onModelTitleChanged);
        taskModel.descriptionProperty().removeListener(onModelDescriptionChanged);
        taskModel.highlightProperty().removeListener(onModelHighlightChanged);
        System.out.println("TaskDetailsPopup: Cleared bindings");
    }

    public interface Factory {
        TaskDetailsPopup create(BoardModel boardModel);
    }
}
