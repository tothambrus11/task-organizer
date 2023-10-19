package client.models;

import client.components.NonRecyclingListView;
import client.contexts.SessionContext;
import client.utils.BigFractionProperty;
import client.utils.UUIDProperty;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.models.Task;
import commons.models.TaskList;
import commons.utils.SmartColor;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class TaskListModel implements Comparable<TaskListModel>, NonRecyclingListView.CanBeDummy {
    // Entity data
    private final StringProperty title = new SimpleStringProperty("");
    private final BigFractionProperty position = new BigFractionProperty(BigFraction.ONE_HALF);
    private final UUIDProperty id;
    // Additional data
    private final ObservableList<TaskModel> observableTaskList = FXCollections.observableArrayList();
    private final SortedList<TaskModel> sortedTasks = new SortedList<>(observableTaskList, TaskModel::compareTo);
    /**
     * Represents the sorted list position of the dragged card in the list before which the dragged card should be
     * inserted. This means that if the card is dragged over the upper half of the first card, this value will be 1, so
     * the card is expected to be inserted before the current card at position 1 in the sorted list.
     * If the card is dragged over the nth card's lower half, this value will be n+1 meaning that the card is expected
     * to be inserted before the n+1st card in the sorted list so to the end of the list (if n is the number of real cards).
     */
    private final SimpleIntegerProperty dragoverPos = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty draggedCardIndex = new SimpleIntegerProperty(-2);
    private final TaskModel.Factory taskModelFactory;
    private final SessionContext sessionContext;
    private BoardModel parentBoard;
    private final ObjectProperty<SmartColor> foregroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<SmartColor> backgroundColor = new SimpleObjectProperty<>();


    /**
     * Creates a new task list with a given id and parent board model
     *
     * @param parentBoard parentBoard this task list belongs to
     * @param id          key of this task list
     */
    @AssistedInject
    public TaskListModel(@Assisted BoardModel parentBoard,
                         @Assisted UUID id,
                         TaskModel.DummyTask.Factory dummyTaskFactory,
                         TaskModel.Factory taskModelFactory,
                         SessionContext sessionContext) {
        this.taskModelFactory = taskModelFactory;
        this.sessionContext = sessionContext;

        this.parentBoard = parentBoard;
        this.id = new UUIDProperty(id);

        var firstDummy = dummyTaskFactory.create(this, BigFraction.ZERO);
        var lastDummy = dummyTaskFactory.create(this, BigFraction.ONE);
        observableTaskList.add(firstDummy);
        observableTaskList.add(lastDummy);
    }

    /**
     * Creates a new task list with a random id and a given board id
     *
     * @param parentBoard board that this task list belongs to
     */
    @AssistedInject
    public TaskListModel(@Assisted BoardModel parentBoard,
                         TaskModel.DummyTask.Factory dummyTaskFactory,
                         TaskModel.Factory taskModelFactory,
                         SessionContext sessionContext) {
        this(parentBoard, UUID.randomUUID(), dummyTaskFactory, taskModelFactory, sessionContext);
    }

    @AssistedInject
    public TaskListModel(@Assisted BoardModel parentBoard,
                         @Assisted TaskList serverTaskList,
                         TaskModel.DummyTask.Factory dummyTaskFactory,
                         TaskModel.Factory taskModelFactory,
                         SessionContext sessionContext) {

        this(parentBoard, serverTaskList.getId(), dummyTaskFactory, taskModelFactory, sessionContext);
        setTitle(serverTaskList.getTitle());
        setPosition(serverTaskList.getPosition());

        for (var serverTask : serverTaskList.getTasks()) {
            observableTaskList.add(taskModelFactory.create(this, serverTask));
        }
    }

    public BoardModel getParentBoard() {
        return parentBoard;
    }

    public void setParentBoard(BoardModel parentBoard) {
        this.parentBoard = parentBoard;
    }

    public BoardModel parentBoardProperty() {
        return parentBoard;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public SmartColor getForegroundColor() {
        return foregroundColor.get();
    }

    public ObjectProperty<SmartColor> foregroundColorProperty() {
        return foregroundColor;
    }

    public void setForegroundColor(SmartColor foregroundColor) {
        this.foregroundColor.set(foregroundColor);
    }

    public SmartColor getBackgroundColor() {
        return backgroundColor.get();
    }

    public ObjectProperty<SmartColor> backgroundColorProperty() {
        return backgroundColor;
    }

    public void setBackgroundColor(SmartColor backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    public UUID getId() {
        return id.get();
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public UUIDProperty idProperty() {
        return id;
    }


    public SortedList<TaskModel> getSortedTasks() {
        return sortedTasks;
    }

    public int taskCount() {
        return sortedTasks.size() - 2;
    }

    public void moveInTaskAfter(TaskModel taskToMove, TaskModel afterTask) {
        if (Objects.equals(taskToMove, afterTask)) {
            throw new IllegalArgumentException("The two tasks should be different");
        }
        var afterTaskIndex = sortedTasks.indexOf(afterTask);
        if (afterTaskIndex == -1) {
            throw new NoSuchElementException("The afterTask is not in the list");
        }

        var beforeTask = sortedTasks.get(afterTaskIndex + 1);

        moveInTask(taskToMove, beforeTask.getPosition().add(afterTask.getPosition()).divide(2));
    }

    public void moveInTaskBefore(TaskModel taskToMove, TaskModel beforeTask) {
        if (Objects.equals(taskToMove, beforeTask)) {
            throw new IllegalArgumentException("The two tasks should be different");
        }
        var beforeTaskIndex = sortedTasks.indexOf(beforeTask);
        if (beforeTaskIndex == -1) {
            throw new NoSuchElementException("The beforeTask is not in the list");
        }
        if (beforeTaskIndex == 0) {
            throw new IllegalArgumentException("The beforeTask cannot be the dummy node");
        }

        var afterTask = sortedTasks.get(beforeTaskIndex - 1);
        moveInTask(taskToMove, beforeTask.getPosition().add(afterTask.getPosition()).divide(2));
    }


    public void moveInTask(TaskModel task, BigFraction position) {
        var sourceList = task.getParentTaskList();
        sourceList.observableTaskList.remove(task);

        task.setParentTaskList(this);
        task.setPosition(position);

        this.observableTaskList.add(task);
        task.triggerFocus();

        System.out.println("the order of the tasks in list is: ");
        for (var t : getSortedTasks()) {
            System.out.println(t.getTitle() + " " + t.getPosition());
        }
        System.out.println();
        System.out.println();

        sessionContext.moveTask(task.getId(), sourceList.getId(), getId(), task.getPosition());
    }

    public TaskModel createTaskBetween(TaskModel first, TaskModel second, Consumer<TaskModel> taskInitializer) {
        if (Objects.equals(first.getPosition(), second.getPosition())) {
            throw new IllegalArgumentException("The two tasks should have different positions");
        }
        if (!Objects.equals(first.getParentTaskList(), second.getParentTaskList())) {
            throw new IllegalArgumentException("The two tasks should be in the same list");
        }

        var newTask = taskModelFactory.create(first.getParentTaskList());
        newTask.setPosition(first.getPosition().add(second.getPosition()).divide(2));
        newTask.setHighlight(getParentBoard().getDefaultHighlight());
        taskInitializer.accept(newTask);

        observableTaskList.add(newTask);
        sessionContext.addTask(newTask.toServerTask());

        return newTask;
    }

    public BigFraction getPosition() {
        return position.get();
    }

    public void setPosition(BigFraction position) {
        this.position.set(position);
    }

    public ObjectProperty<BigFraction> positionProperty() {
        return position;
    }

    public TaskModel createTaskAfter(TaskModel afterThis, Consumer<TaskModel> taskInitializer) {
        var index = sortedTasks.indexOf(afterThis);
        if (index == -1) {
            throw new NoSuchElementException();
        }
        if (index == sortedTasks.size() - 1) {
            throw new IllegalArgumentException("Cannot create task after the dummy task");
        }
        var nextTask = sortedTasks.get(index + 1);
        return createTaskBetween(afterThis, nextTask, taskInitializer);
    }

    public TaskModel createTaskBefore(TaskModel beforeThis, Consumer<TaskModel> taskInitializer) {
        var index = sortedTasks.indexOf(beforeThis);
        if (index == -1) {
            throw new NoSuchElementException();
        }
        if (index == 0) {
            throw new IllegalArgumentException("Cannot create task before the dummy task");
        }
        var prevTask = sortedTasks.get(index - 1);
        return createTaskBetween(prevTask, beforeThis, taskInitializer);
    }

    public TaskModel createTaskAtEnd(Consumer<TaskModel> taskInitializer) {
        return createTaskBefore(getLastDummy(), taskInitializer);
    }

    /**
     * Returns the last task in the list, or if the list doesn't contain actual tasks, it returns the first dummy task,
     * which is not displayed nor saved on the server, just used as a reference point.
     */
    public TaskModel getLastTask() {
        return sortedTasks.get(sortedTasks.size() - 2);
    }

    public int getDragoverPos() {
        return dragoverPos.get();
    }

    public SimpleIntegerProperty dragoverPosProperty() {
        return dragoverPos;
    }

    public int getDraggedCardIndex() {
        return draggedCardIndex.get();
    }

    public SimpleIntegerProperty draggedCardIndexProperty() {
        return draggedCardIndex;
    }

    public TaskModel getLastDummy() {
        return sortedTasks.get(sortedTasks.size() - 1);
    }

    @Override
    public int compareTo(TaskListModel o) {
        return position.get().compareTo(o.position.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListModel that = (TaskListModel) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isDummy() {
        return false;
    }

    public TaskModel getTaskById(UUID taskId) {
        return observableTaskList.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> {

                    var a = new NoSuchElementException("Task with id " + taskId + " not found");
                    a.printStackTrace();

                    return a;
                });
    }

    public void peerAddedTask(Task task) {
        var taskModel = taskModelFactory.create(this, task);
        observableTaskList.add(taskModel);
    }

    public void peerAddedTask(TaskModel task) {
        observableTaskList.add(task);
    }

    public void peerUpdatedTask(Task task) {
        var taskModel = getTaskById(task.getId());
        taskModel.peerUpdatedTask(task);
    }

    public void peerRemovedTask(TaskModel task) {
        observableTaskList.remove(task);
        task.getOnDelete().run();
    }

    public void removeTask(TaskModel taskModel) {
        observableTaskList.remove(taskModel); // todo close the details view if it's open
        sessionContext.removeTask(taskModel.getId(), taskModel.getParentTaskList().getId());
    }

    public void save() {
        sessionContext.updateTaskList(this.toServerTaskList());
    }

    public TaskList toServerTaskList() {
        var taskList = new TaskList();
        taskList.setId(getId());
        taskList.setTitle(getTitle());
        taskList.setPosition(getPosition());
        return taskList;
    }

    public void remove() {
        parentBoard.removeTaskList(this);
    }

    public void peerUpdated(TaskList taskList) {
        this.title.set(taskList.getTitle());
    }

    /**
     * Called when a card is dropped on this list. If the card was dropped on the list itself, it is moved to the end of
     * the list. If it was dropped on a card, it is moved before or after the card based on the dragOverPos property of
     * the list.
     *
     * @param dragBoardContent information about the source card. The data is in the format
     *                         sourceListId sourceTaskId
     * @return true if the card was drop was handled successfully, false otherwise.
     */
    public boolean onCardDropped(String dragBoardContent) {
        var parts = dragBoardContent.split(" ");
        if (parts.length != 2) {
            return false;
        }
        String sourceListId = parts[0];
        String taskId = parts[1];

        var sourceList = parentBoard.getListById(UUID.fromString(sourceListId));

        var sourceTask = sourceList.getTaskById(UUID.fromString(taskId));

        if (getDragoverPos() == -1) {
            moveInTaskBefore(sourceTask, getLastDummy());
        } else {
            var afterTask = getSortedTasks().get(getDragoverPos() - 1);
            moveInTaskAfter(sourceTask, afterTask);
        }

        dragoverPosProperty().set(-1);
        sourceTask.triggerFocus();
        return true;
    }

    public TaskModel getFirstDummy() {
        return sortedTasks.get(0);
    }

    public void peerMovedOutTask(TaskModel task) {
        observableTaskList.remove(task);
    }

    public interface Factory {
        TaskListModel create(BoardModel parentBoard, UUID id);

        TaskListModel create(BoardModel parentBoard);

        TaskListModel create(BoardModel parentBoard, TaskList serverTaskList);
    }

    public static class DummyTaskListModel extends TaskListModel {

        @Inject
        public DummyTaskListModel(@Assisted BoardModel parentBoard,
                                  @Assisted BigFraction position,
                                  TaskModel.DummyTask.Factory dummyTaskFactory,
                                  TaskModel.Factory taskModelFactory,
                                  SessionContext sessionContext) {
            super(parentBoard, dummyTaskFactory, taskModelFactory, sessionContext);
            this.positionProperty().set(position);
        }

        @Override
        public boolean isDummy() {
            return true;
        }

        public interface Factory {
            DummyTaskListModel create(BoardModel parentBoard, BigFraction position);
        }
    }
}
