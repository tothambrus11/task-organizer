package client.views;

import client.App;
import client.SimpleFXMLLoader;
import client.components.AutoSizedTextArea;
import client.components.IconButton;
import client.models.BoardModel;
import client.models.HighlightModel;
import client.models.TaskListModel;
import client.models.TaskModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

public class TaskCellContent extends VBox {
    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private final TaskListModel taskListModel;
    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);
    private final TagHighlightListOnCard.Factory tagHighlightListOnCardFactory;
    private boolean topAnimationTargetState = false;
    private boolean bottomAnimationTargetState = false;
    private Timeline topAnimation;
    private Timeline bottomAnimation;
    @FXML
    private VBox card;
    @FXML
    private AnchorPane dropAreaTop;
    @FXML
    private AnchorPane dropAreaBottom;
    @FXML
    private AutoSizedTextArea title;
    @FXML
    private IconButton editButton;
    @FXML
    private VBox tagListContainer;
    @FXML
    private VBox progressContainer;
    private TaskModel task;
    ChangeListener<String> onModelTitleChanged = (observable, oldValue, newValue) -> {
        title.setText(task.getTitle());
    };
    private int index = -1;
    @FXML
    private Circle descriptionIndicator;
    private App app;
    private TaskProgressView taskProgressView;

    @Inject
    public TaskCellContent(@Assisted TaskListModel taskListModel,
                           App app,
                           TagHighlightListOnCard.Factory tagHighlightListOnCardFactory,
                           TaskProgressView.Factory taskProgressViewFactory,
                           SimpleFXMLLoader fxmlLoader) {
        super();
        this.taskListModel = taskListModel;
        this.tagHighlightListOnCardFactory = tagHighlightListOnCardFactory;
        this.app = app;

        fxmlLoader.initView("/client/components/TaskListItemInner.fxml", this);

        getStyleClass().add("TaskListItemInner");

        card.setFocusTraversable(true);


        // initialize the progress view
        taskProgressView = taskProgressViewFactory.create();
        progressContainer.getChildren().add(taskProgressView);

        setupDragAndDrop();
        setupTitleEditing();
        setupContextMenu();
        setupShortcuts();

    }

    private void setupShortcuts() {
        setOnKeyPressed(event -> {
            var board = taskListModel.getParentBoard();
            var listIndex = board.getTaskListModelsSorted().indexOf(taskListModel);

            if (event.isShiftDown()) {
                switch (event.getCode()) {
                    case UP -> {
                        board.moveTaskInDirection(BoardModel.NavigationDirection.UP, listIndex, index);
                        event.consume();
                    }
                    case DOWN -> {
                        board.moveTaskInDirection(BoardModel.NavigationDirection.DOWN, listIndex, index);
                        event.consume();
                    }
                    case LEFT -> {
                        board.moveTaskInDirection(BoardModel.NavigationDirection.LEFT, listIndex, index);
                        event.consume();
                    }
                    case RIGHT -> {
                        board.moveTaskInDirection(BoardModel.NavigationDirection.RIGHT, listIndex, index);
                        event.consume();
                    }
                }
            } else {
                switch (event.getCode()) {
                    case UP -> {
                        board.navigate(BoardModel.NavigationDirection.UP, listIndex, index);
                        event.consume();
                    }
                    case DOWN -> {
                        board.navigate(BoardModel.NavigationDirection.DOWN, listIndex, index);
                        event.consume();
                    }
                    case LEFT -> {
                        board.navigate(BoardModel.NavigationDirection.LEFT, listIndex, index);
                        event.consume();
                    }
                    case RIGHT -> {
                        board.navigate(BoardModel.NavigationDirection.RIGHT, listIndex, index);
                        event.consume();
                    }
                }
            }

            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                actionDelete(event);
                event.consume();
            }
            if (event.getCode() == KeyCode.E) {
                event.consume();
                Platform.runLater(() -> {
                    actionEditInline(event);
                });
            }
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                openTaskDetails();
            }
        });
    }

    private void openTaskDetails() {
        app.getBoardSceneCtrl().viewTaskDetails(task);
    }

    public void cancelTitleEdit() {
        title.setText(task.getTitle());
        isEditing.set(false);
        title.deselect();
    }

    public void setupTitleEditing() {
        title.editableProperty().bind(isEditing);

        editButton.setOnAction(event -> {
            if (isEditing.get()) {
                finishTitleEdit();
            } else {
                actionEditInline(event);
            }
        });

        isEditing.addListener((observable, oldValue, newValue) -> {
            if (isEditing.get()) {
                System.out.println("isEditing");
                editButton.setSource("/client/icons/check_small.png");
            } else {
                System.out.println("isNotEditing");
                editButton.setSource("/client/icons/edit_small.png");
            }
        });

        editButton.opacityProperty().bind(Bindings.createDoubleBinding(() -> editButton.isHover() || !isEditing.get() ? 1.0 : 0.5, editButton.hoverProperty(), isEditing));
        editButton.visibleProperty().bind(card.hoverProperty());
        title.mouseTransparentProperty().bind(isEditing.not());

        card.setOnMousePressed(event -> {
            card.requestFocus();
        });
        card.setOnMouseEntered(e -> {
            if (!isEditing.get()) card.requestFocus();
        });

        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTaskDetails();
            }
        });

        title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                finishTitleEdit();
            }
        });
        title.focusTraversableProperty().bind(isEditing);


        title.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == ENTER) {
                if (event.isShiftDown() || event.isControlDown()) {
                    title.appendText("\n");
                } else {
                    event.consume();
                    finishTitleEdit();
                    card.requestFocus();
                }
            }

            if (event.getCode() == ESCAPE) {
                cancelTitleEdit();
                event.consume();
                card.requestFocus();
            }
        });
    }

    public void finishTitleEdit() {
        isEditing.set(false);
        title.deselect();

        if (title.getText().equals(task.getTitle())) return;
        task.setTitle(title.getText());
        task.save();
    }

    public void actionEditInline(Event e) {
        isEditing.set(true);
        title.requestFocus();

        // Select all text after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Platform.runLater(() -> title.selectAll());
        }).start();
    }

    public void actionDelete(Event e) {
        task.remove();
    }

    private void setupContextMenu() {
        var delete = new MenuItem("Delete (Del)");
        delete.setOnAction(this::actionDelete);

        var editInline = new MenuItem("Edit Title (E)");
        editInline.setOnAction(this::actionEditInline);

        var openDetails = new MenuItem("Open Details (Enter)");
        openDetails.setOnAction(event -> openTaskDetails());

        var contextMenu = new ContextMenu(delete, editInline, openDetails);

        card.setOnContextMenuRequested(event -> {
            contextMenu.show(card, event.getScreenX(), event.getScreenY());
        });
    }

    private void setupDragAndDrop() {
        card.setOnDragDetected(event -> {
            var dragBoard = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(task.getParentTaskList().getId() + " " + task.getId());
            dragBoard.setContent(content);

            taskListModel.draggedCardIndexProperty().set(index);
            card.setOpacity(0.5);
            event.consume();
        });

        setOnDragDropped((e) -> {
            if (e.getDragboard().hasString()) {
                if (taskListModel.onCardDropped(e.getDragboard().getString())) {
                    e.setDropCompleted(true);
                    e.consume();
                }
            }
            hideDragOverInstantly();
        });

        card.setOnDragDone(e -> {
            taskListModel.dragoverPosProperty().set(-1);
            taskListModel.draggedCardIndexProperty().set(-1);

            card.setOpacity(1);
        });

        setOnDragExited(e -> taskListModel.dragoverPosProperty().set(-1));

        setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                if (event.getSceneY() < card.localToScene(card.getBoundsInLocal()).getCenterY()) {
                    // inserting above this card
                    if (taskListModel.draggedCardIndexProperty().get() != index && taskListModel.draggedCardIndexProperty().get() != index - 1) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        taskListModel.dragoverPosProperty().set(index);
                    } else {
                        taskListModel.dragoverPosProperty().set(-1);
                    }

                } else {
                    // inserting below this task
                    if (taskListModel.draggedCardIndexProperty().get() != index && taskListModel.draggedCardIndexProperty().get() != index + 1) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        taskListModel.dragoverPosProperty().set(index + 1);
                    } else {
                        taskListModel.dragoverPosProperty().set(-1);
                    }
                }
                event.consume();
            }

        });
    }

    public void hideDragOverInstantly() {
        if (topAnimation != null) {
            topAnimation.stop();
            topAnimation = null;
        }
        if (bottomAnimation != null) {
            bottomAnimation.stop();
            bottomAnimation = null;
        }

        dropAreaBottom.setOpacity(0);
        dropAreaBottom.setPrefHeight(0);

        dropAreaTop.setOpacity(0);
        dropAreaTop.setPrefHeight(0);
    }

    void refreshHighlightBinding(){
        if(task.getHighlight() == null) {
            return;
        }
        card.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (task.getHighlight() == null) return "";
            return "-fx-background-color: " + task.getHighlight().getBackgroundColor() +
                    "; -fx-text-fill: " + task.getHighlight().getForegroundColor();
        }, task.getHighlight().backgroundColorProperty(), task.getHighlight().foregroundColorProperty()));

        title.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (task.getHighlight() == null) return "";
            return "-fx-text-fill: " + task.getHighlight().getForegroundColor();
        }, task.getHighlight().foregroundColorProperty()));

        editButton.backgroundColorProperty().bind(task.getHighlight().backgroundColorProperty());
        editButton.foregroundColorProperty().bind(task.getHighlight().foregroundColorProperty());

    }
    ChangeListener<HighlightModel> onHighlightChanged = (observable, oldValue, newValue) -> {
        refreshHighlightBinding();
    };
     public void setTask(TaskModel task, int index) {
        System.out.println("set task and clear bindings");
        clearBindings();

        this.index = index;
        this.task = task;

        task.titleProperty().addListener(onModelTitleChanged);
        title.setText(task.getTitle());
        task.setRequestFocusCallback(() -> {
            Platform.runLater(card::requestFocus);
        });
        descriptionIndicator.visibleProperty().bind(task.descriptionProperty().isNotEmpty());
        task.highlightProperty().addListener(onHighlightChanged);
        refreshHighlightBinding();
        refreshTagList();

        taskProgressView.setTask(task);

        System.out.println("set task and clear bindings done");
    }

    private void refreshTagList() {
        if (!tagListContainer.getChildren().isEmpty()) {
            var tagList = (TagHighlightListOnCard) tagListContainer.getChildren().get(0);
            tagList.onDestroy();
            tagListContainer.getChildren().clear();
        }
        tagListContainer.getChildren().add(tagHighlightListOnCardFactory.create(task.getTags()));
    }


    /**
     * Removes all subscriptions to the model, so that the card can be reused
     */
    public void clearBindings() {
        if (task != null) {
            task.titleProperty().removeListener(onModelTitleChanged);
            task.highlightProperty().removeListener(onHighlightChanged);
        }
    }

    public void setShowDragOverTop(boolean show) {
        System.out.println("show top: " + show);
        if (topAnimationTargetState == show) return;
        topAnimationTargetState = show;
        if (topAnimation != null) {
            topAnimation.stop();
        }
        topAnimation = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(dropAreaTop.prefHeightProperty(), show ? 58 : 0, Interpolator.EASE_BOTH),
                        new KeyValue(dropAreaTop.opacityProperty(), show ? 1 : 0, Interpolator.EASE_BOTH)
                ));

        topAnimation.play();
    }

    public void setShowDragOverBottom(boolean show) {
        System.out.println("show bottom: " + show);
        if (bottomAnimationTargetState == show) return;
        bottomAnimationTargetState = show;

        if (bottomAnimation != null) {
            bottomAnimation.stop();
        }
        bottomAnimation = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(dropAreaBottom.prefHeightProperty(), show ? 58 : 0, Interpolator.EASE_BOTH),
                        new KeyValue(dropAreaBottom.opacityProperty(), show ? 1 : 0, Interpolator.EASE_BOTH)
                ));

        bottomAnimation.play();
    }

    public interface Factory {
        TaskCellContent create(TaskListModel taskListModel);
    }
}
