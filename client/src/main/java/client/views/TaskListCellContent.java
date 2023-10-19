package client.views;

import client.SimpleFXMLLoader;
import client.components.AddButton;
import client.components.AutoSizedTextArea;
import client.components.IconButton;
import client.models.TaskListModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class TaskListCellContent extends VBox {


    private final TaskListView taskListView;
    private final TaskListModel taskList;
    private final AddButton.Factory addButtonFactory;
    @FXML
    private AutoSizedTextArea title;
    private final ChangeListener<String> onModelTitleChanged = (observable, oldValue, newValue) -> title.setText(newValue);
    @FXML
    private VBox listContainer;
    @FXML
    private IconButton deleteListButton;
    @FXML
    private AddButton addCardButton;
    @FXML
    private AnchorPane addButtonContainer;

    @Inject
    public TaskListCellContent(@Assisted TaskListModel taskList,
                               TaskListView.Factory taskListViewFactory,
                               SimpleFXMLLoader fxmlLoader,
                               AddButton.Factory addButtonFactory) {
        super();
        this.taskList = taskList;
        this.addButtonFactory = addButtonFactory;

        fxmlLoader.initView("/client/views/TaskListCellContent.fxml", this);

        taskListView = taskListViewFactory.create(taskList);
        listContainer.getChildren().add(taskListView);
        taskListView.prefHeightProperty().bind(listContainer.heightProperty());

        VBox.setVgrow(listContainer, Priority.ALWAYS);

        getStyleClass().add("TaskListCellContent");
        styleProperty().bind(taskList.getParentBoard().listBackgroundColorProperty().asString("-fx-background-color: %s;"));

        setupDnD(taskList);

        setupDeleteListButton(taskList);
        setupTitleEditing(taskList);
        setupAddButton(taskList);
    }

    private void setupDnD(TaskListModel taskList) {
        setOnDragDropped((e) -> {
            if (e.getDragboard().hasString()) {
                if (taskList.onCardDropped(e.getDragboard().getString())) {
                    e.setDropCompleted(true);
                    e.consume();
                }
            }
        });
        setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
    }

    private void setupDeleteListButton(TaskListModel taskList) {
        var shouldBeTransparent = Bindings.createBooleanBinding(() -> title.isFocused() && !deleteListButton.isHover(), title.focusedProperty(), deleteListButton.hoverProperty());
        var opacity = Bindings.createDoubleBinding(() -> shouldBeTransparent.get() ? 0.5 : 1.0, shouldBeTransparent);
        deleteListButton.opacityProperty().bind(opacity);
        deleteListButton.setOnAction(event -> taskList.remove());
        deleteListButton.backgroundColorProperty().bind(taskList.getParentBoard().listBackgroundColorProperty());
        deleteListButton.foregroundColorProperty().bind(taskList.getParentBoard().listForegroundColorProperty());
    }

    private void setupTitleEditing(TaskListModel taskList) {
        taskList.titleProperty().addListener(onModelTitleChanged);
        title.setText(taskList.getTitle());

        title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !title.getText().equals(taskList.getTitle())) {
                taskList.setTitle(title.getText());
                taskList.save();
            }
        });

        title.styleProperty().bind(taskList.getParentBoard().listForegroundColorProperty().asString("-fx-text-fill: %s;"));
    }

    private void setupAddButton(TaskListModel taskList) {
        addCardButton = addButtonFactory.create();
        AnchorPane.setLeftAnchor(addCardButton, 12.0);
        AnchorPane.setRightAnchor(addCardButton, 12.0);
        AnchorPane.setBottomAnchor(addCardButton, 12.0);
        AnchorPane.setTopAnchor(addCardButton, 12.0);
        addCardButton.setText("+ Add Card");
        addButtonContainer.getChildren().add(addCardButton);

        addCardButton.setOnAction(event -> taskList.createTaskAtEnd(task -> task.setTitle("New Task")));
        addCardButton.backgroundColorProperty().bind(taskList.getParentBoard().listBackgroundColorProperty());
        addCardButton.foregroundColorProperty().bind(taskList.getParentBoard().listForegroundColorProperty());
    }

    public void onRemoved() {
        taskList.titleProperty().removeListener(onModelTitleChanged);
    }

    public interface Factory {
        TaskListCellContent create(TaskListModel taskList);
    }
}
