package client.views;

import client.components.AddButton;
import client.components.NonRecyclingListView;
import client.models.BoardModel;
import client.models.TaskListModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;


public class BoardView extends ScrollPane {
    private final TaskListCell.Factory taskListCellFactory;
    private final BoardModel boardModel;
    private final AddButton.Factory addButtonFactory;
    NonRecyclingListView<TaskListModel> boardListView;

    NonRecyclingListView.ListCellFactory<TaskListModel> cellFactory = new NonRecyclingListView.ListCellFactory<>() {
        @Override
        @SuppressWarnings("unchecked")
        public <U extends Node & NonRecyclingListView.ListCell<TaskListModel>> U createCell(TaskListModel taskList) {
            return (U) taskListCellFactory.create(taskList);
        }
    };

    @Inject
    public BoardView(@Assisted BoardModel boardModel, TaskListCell.Factory taskListCellFactory, AddButton.Factory addButtonFactory) {
        this.taskListCellFactory = taskListCellFactory;
        this.addButtonFactory = addButtonFactory;
        this.boardModel = boardModel;
        getStyleClass().add("BoardView");

        var horizontalContainer = new HBox();
        horizontalContainer.setSpacing(15);
        horizontalContainer.setPadding(new Insets(15, 15, 15, 15));
        styleProperty().bind(boardModel.boardBackgroundColorProperty().asString("-fx-background-color: %s !important;"));
        horizontalContainer.getChildren().add(initListView());
        horizontalContainer.getChildren().add(initAddButton());
        setContent(horizontalContainer);
    }

    NonRecyclingListView<TaskListModel> initListView() {
        boardListView = new NonRecyclingListView<>(Orientation.HORIZONTAL, boardModel.getTaskListModelsSorted(), cellFactory);
        var listContainer = (HBox) boardListView.getContainer();
        listContainer.setSpacing(15);


        var actualHeight = Bindings.createDoubleBinding(() -> getViewportBounds().getHeight() - 32, viewportBoundsProperty());

        boardListView.prefHeightProperty().bind(actualHeight);
        boardListView.getContainer().prefHeightProperty().bind(actualHeight);

        return boardListView;
    }

    AddButton initAddButton() {
        var addButton = addButtonFactory.create();
        addButton.setText("+ Add List");
        addButton.setOnAction(event -> {
            boardModel.createTaskListAtEnd(taskListModel -> taskListModel.setTitle("New List"));
        });
        addButton.setPrefWidth(300);
        addButton.backgroundColorProperty().bind(boardModel.listBackgroundColorProperty().map(color -> color.transparentize(0.5)));
        addButton.foregroundColorProperty().bind(boardModel.listForegroundColorProperty());
        getChildren().add(addButton);


        return addButton;
    }

    public interface Factory {
        BoardView create(BoardModel boardModel);
    }


}
