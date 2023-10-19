package client.models;

import client.contexts.SessionContext;
import client.utils.UUIDProperty;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.messages.bidirectional.MoveTaskMessage;
import commons.models.*;
import commons.utils.SmartColor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class BoardModel {
    private final UUIDProperty id;
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty creator = new SimpleStringProperty("");
    private final StringProperty key = new SimpleStringProperty("");
    private final ObservableList<TaskListModel> taskListModels = FXCollections.observableArrayList();
    private final ObservableList<TagModel> tagModels = FXCollections.observableArrayList();

    private final SortedList<TaskListModel> taskListModelsSorted = new SortedList<>(taskListModels, TaskListModel::compareTo);
    private final TaskListModel.Factory taskListModelFactory;
    private final ObservableList<HighlightModel> highlightModels = FXCollections.observableArrayList();
    private final SortedList<HighlightModel> highlightModelsSorted = new SortedList<>(highlightModels, HighlightModel::compareTo);
    private final SessionContext sessionContext;
    private final TagModel.Factory tagModelFactory;
    private final ObjectProperty<SmartColor> boardForegroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<SmartColor> boardBackgroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<SmartColor> listForegroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<SmartColor> listBackgroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<HighlightModel> defaultHighlight = new SimpleObjectProperty<>();

    /**
     * Creates a new board with a given id
     *
     * @param id key of this board
     */
    @AssistedInject
    public BoardModel(@Assisted UUID id, TagModel.Factory tagModelFactory,
                      TaskListModel.DummyTaskListModel.Factory dummyTaskListModelFactory,
                      TaskListModel.Factory taskListModelFactory, SessionContext sessionContext) {
        this.tagModelFactory = tagModelFactory;
        this.taskListModelFactory = taskListModelFactory;
        this.sessionContext = sessionContext;

        this.id = new UUIDProperty(id);

        var firstDummy = dummyTaskListModelFactory.create(this, BigFraction.ZERO);
        var lastDummy = dummyTaskListModelFactory.create(this, BigFraction.ONE);
        taskListModels.add(firstDummy);
        taskListModels.add(lastDummy);
    }

    @AssistedInject
    public BoardModel(@Assisted Board serverBoard, TagModel.Factory tagModelFactory, TaskListModel.DummyTaskListModel.Factory dummyTaskListModelFactory, TaskListModel.Factory taskListModelFactory, SessionContext sessionContext) {
        this(serverBoard.getId(), tagModelFactory, dummyTaskListModelFactory, taskListModelFactory, sessionContext);

        setTitle(serverBoard.getTitle());
        setKey(serverBoard.getKey());
        setCreator(serverBoard.getCreator());
        setBoardBackgroundColor(serverBoard.getBoardBackgroundColor());
        setBoardForegroundColor(serverBoard.getBoardForegroundColor());
        setListBackgroundColor(serverBoard.getListBackgroundColor());
        setListForegroundColor(serverBoard.getListForegroundColor());

        // add tags
        for (var serverTag : serverBoard.getTags()) {
            tagModels.add(tagModelFactory.create(serverTag, this));
        }

        // add highlights
        for (var serverHighlight : serverBoard.getHighlights()) {
            highlightModels.add(new HighlightModel(serverHighlight, this, sessionContext));
        }
        // find default highlight
        defaultHighlight.set(highlightModels.stream().filter(h -> h.getId().equals(serverBoard.getDefaultHighlightId())).findFirst().orElseThrow(() -> new NoSuchElementException("No default highlight found")));

        // add task lists
        for (var serverTaskList : serverBoard.getTaskLists()) {
            taskListModels.add(taskListModelFactory.create(this, serverTaskList));
        }
    }

    public SortedList<HighlightModel> getHighlightModels() {
        return highlightModelsSorted;
    }

    public SmartColor getListForegroundColor() {
        return listForegroundColor.get();
    }

    public void setListForegroundColor(SmartColor listForegroundColor) {
        this.listForegroundColor.set(listForegroundColor);
    }

    public ObjectProperty<SmartColor> listForegroundColorProperty() {
        return listForegroundColor;
    }

    public SmartColor getListBackgroundColor() {
        return listBackgroundColor.get();
    }

    public void setListBackgroundColor(SmartColor listBackgroundColor) {
        this.listBackgroundColor.set(listBackgroundColor);
    }

    public ObjectProperty<SmartColor> listBackgroundColorProperty() {
        return listBackgroundColor;
    }

    public String getKey() {
        return key.get();
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public StringProperty keyProperty() {
        return key;
    }

    public void peerMovedTask(MoveTaskMessage moveInfo) {
        var sourceList = getListById(moveInfo.getSourceListId());
        var targetList = getListById(moveInfo.getTargetListId());

        var task = sourceList.getTaskById(moveInfo.getTaskId());
        sourceList.peerMovedOutTask(task);
        task.setPosition(moveInfo.getTargetPosition());
        targetList.peerAddedTask(task);

        System.out.println("Peer moved task " + task.getId() + " from " + sourceList.getId() + " to " + targetList.getId());
        System.out.println("Positions of tasks in list are:");
        for (var t : targetList.getSortedTasks()) {
            System.out.println(t.getTitle() + " " + t.getPosition());
        }
        System.out.println();

    }

    /**
     * Creates a new board with a random id
     */
    // private BoardModel() {
    //     this(UUID.randomUUID());
    // }
    public String getCreator() {
        return creator.get();
    }

    public void setCreator(String creator) {
        this.creator.set(creator);
    }

    public StringProperty creatorProperty() {
        return creator;
    }

    public SortedList<TaskListModel> getTaskListModelsSorted() {
        return taskListModelsSorted;
    }

    public TaskListModel createTaskListBetween(TaskListModel first, TaskListModel second, UUID taskListId, Consumer<TaskListModel> initializer) {
        if (first.getPosition().equals(second.getPosition())) {
            throw new IllegalArgumentException("The two lists should have different positions");
        }

        var newList = taskListModelFactory.create(this, taskListId);

        newList.setPosition(first.getPosition().add(second.getPosition()).divide(2));
        initializer.accept(newList);

        taskListModels.add(newList);
        sessionContext.addTaskList(newList.toServerTaskList());
        return newList;
    }

    /**
     * Creates a new task list between two existing task lists with a random id
     *
     * @param first  the new task list will be created after this one
     * @param second the new task list will be created before this one
     */
    public TaskListModel createTaskListBetween(TaskListModel first, TaskListModel second, Consumer<TaskListModel> initializer) {
        return createTaskListBetween(first, second, UUID.randomUUID(), initializer);
    }

    public TaskListModel createTaskListAfter(TaskListModel afterThis, Consumer<TaskListModel> initializer) {
        var index = taskListModelsSorted.indexOf(afterThis);
        if (index == -1) {
            throw new NoSuchElementException();
        }
        if (index == taskListModelsSorted.size() - 1) {
            throw new IllegalArgumentException("Cannot create task after the dummy task");
        }
        var nextTask = taskListModelsSorted.get(index + 1);
        return createTaskListBetween(afterThis, nextTask, initializer);
    }

    public TaskListModel createTaskListBefore(TaskListModel beforeThis, Consumer<TaskListModel> initializer) {
        var index = taskListModelsSorted.indexOf(beforeThis);
        if (index == -1) {
            throw new NoSuchElementException();
        }
        if (index == 0) {
            throw new IllegalArgumentException("Cannot create task before the dummy task");
        }
        var prevTask = taskListModelsSorted.get(index - 1);
        return createTaskListBetween(prevTask, beforeThis, initializer);
    }

    public TaskListModel createTaskListAtEnd(Consumer<TaskListModel> initializer) {
        return createTaskListBefore(getLastDummy(), initializer);
    }

    public TagModel createTag(String name, SmartColor color) {
        TagModel tagModel = tagModelFactory.create(UUID.randomUUID(), this, name, color);

        if (tagModel.getName() == null || tagModel.getName().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null");
        }
        tagModels.add(tagModel);
        sessionContext.addTag(tagModel.toServerTag());
        return tagModel;
    }

    public TaskListModel getLastTaskList() {
        return taskListModelsSorted.get(taskListModelsSorted.size() - 2);
    }

    public TaskListModel getLastDummy() {
        return taskListModelsSorted.get(taskListModelsSorted.size() - 1);
    }

    public TaskListModel getFirstDummy() {
        return taskListModelsSorted.get(0);
    }

    public TaskListModel getListById(UUID listId) {
        return taskListModelsSorted.stream()
                .filter(taskListModel -> taskListModel.getId().equals(listId))
                .findFirst()
                .orElseThrow();
    }

//    public void removeHighlight(HighlightModel highlightModel) {
//        if (highlightModel.equals(defaultHighlight.get())) {
//            throw new IllegalArgumentException("Cannot remove default highlight");
//        }
//        highlightModels.remove(highlightModel);
//        for (var taskListModel : taskListModels) {
//            for (var task : taskListModel.getSortedTasks()) {
//                if (task.getHighlight().equals(highlightModel)) {
//                    task.setHighlight(defaultHighlight.get());
//                }
//            }
//        }
//        sessionContext.removeHighlight(highlightModel.getId());
//    }

    public UUID getId() {
        return id.get();
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public UUIDProperty idProperty() {
        return id;
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


    public SmartColor getBoardForegroundColor() {
        return boardForegroundColor.get();
    }

    public void setBoardForegroundColor(SmartColor boardForegroundColor) {
        this.boardForegroundColor.set(boardForegroundColor);
    }

    public ObjectProperty<SmartColor> boardForegroundColorProperty() {
        return boardForegroundColor;
    }

    public SmartColor getBoardBackgroundColor() {
        return boardBackgroundColor.get();
    }

    public void setBoardBackgroundColor(SmartColor boardBackgroundColor) {
        this.boardBackgroundColor.set(boardBackgroundColor);
    }

    public void createHighlight(String name, SmartColor foregroundColor, SmartColor backgroundColor) {
        var h=new HighlightModel(
                UUID.randomUUID(),
                name,
                foregroundColor,
                backgroundColor,
                this, getNextHighlightPosition(),
                sessionContext
        );
        highlightModels.add(h);

        sessionContext.createHighlight(h.toServerHighlight());
    }

    public Long getNextHighlightPosition() {
        return highlightModels.stream()
                .map(HighlightModel::getPosition)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    public ObjectProperty<SmartColor> boardBackgroundColorProperty() {
        return boardBackgroundColor;
    }

    public void createTag(TagModel tagModel) {
        tagModels.add(tagModel);
    }

    public ObservableList<TagModel> getTagModels() {
        return tagModels;
    }

    public void removeTaskList(TaskListModel taskListToRemove) {
        taskListModels.remove(taskListToRemove);
        sessionContext.removeTaskList(taskListToRemove.getId());
        // todo close task details when needed
    }

    /**
     * Called when a tag is deleted. This will remove the tag from all tasks. It doesn't notify the server about the
     * changes because the server will figure out this automatically.
     *
     * @param deletedTagId the id of the deleted tag
     */
    public void onDeleteTag(UUID deletedTagId) {
        tagModels.removeIf(tag -> tag.getId().equals(deletedTagId));
        for (var taskListModel : taskListModels) {
            for (TaskModel task : taskListModel.getSortedTasks()) {
                task.onTagDeleted(deletedTagId);
            }
        }
    }

    public void deleteTag(TagModel tagToRemove) {
        onDeleteTag(tagToRemove.getId());
        sessionContext.removeTag(tagToRemove.getId());
    }

    public void peerAddedTaskList(TaskList taskList) {
        taskListModels.add(taskListModelFactory.create(this, taskList));
    }

    public void peerRemovedTaskList(UUID taskListId) {
        var taskList = getListById(taskListId);
        taskListModels.remove(taskList);
    }

    public void peerUpdatedTaskList(TaskList taskList) {
        var taskListModel = getListById(taskList.getId());
        taskListModel.peerUpdated(taskList);
    }

    public void peerUpdatedBoard(Board board) {
        setTitle(board.getTitle());
        setBoardForegroundColor(board.getBoardForegroundColor());
        setBoardBackgroundColor(board.getBoardBackgroundColor());
        setListForegroundColor(board.getListForegroundColor());
        setListBackgroundColor(board.getListBackgroundColor());
    }

    public void save() {
        sessionContext.updateBoard(toServerBoard());
    }

    private Board toServerBoard() {
        Board b = new Board();
        b.setKey(getKey());
        b.setTitle(getTitle());
        b.setCreator(getCreator());
        b.setId(getId());
        b.setBoardBackgroundColor(getBoardBackgroundColor());
        b.setBoardForegroundColor(getBoardForegroundColor());
        b.setListBackgroundColor(getListBackgroundColor());
        b.setListForegroundColor(getListForegroundColor());
        b.setDefaultHighlightId(defaultHighlight.get().getId());
        return b;
    }

    /**
     * Navigates to the direction specified, from the given list and task index. If the navigation wouldn't be possible,
     * it should jump to the next possible task (up -> prev list last task, down -> next list first task, left -> first
     * task on the board, right -> last task on the board)
     *
     * @param direction       the direction to navigate
     * @param listIndex       the index of the list in the sorted list
     *                        (the first list is the dummy list, so the first real list is at index 1)
     * @param taskIndexInList the index of the task in the list (the first task is at index 1)
     */
    // todo navigate to next AVAILABLE task in the next AVAILABLE list. Currently we just try to naviagete to the next ones.
    public void navigate(NavigationDirection direction, int listIndex, int taskIndexInList) {
        System.out.println("Navigating " + direction + " from list " + listIndex + " task " + taskIndexInList);
        switch (direction) {
            case LEFT -> {
                if (listIndex <= 1) {
                    // navigate to the first task on the board
                    if (listCount() > 0 && getTaskListModelsSorted().get(1).taskCount() > 0) {
                        getTaskListModelsSorted().get(1).getSortedTasks().get(1).triggerFocus();
                    }
                    return;
                }
                var prevList = taskListModelsSorted.get(listIndex - 1);

                if (prevList.taskCount() <= 0) return;
                var prevTask = prevList.getSortedTasks().get(Math.min(taskIndexInList, prevList.taskCount()));
                prevTask.triggerFocus();
            }
            case RIGHT -> {
                if (listIndex >= listCount()) {// If we are at the last list, we should navigate to the last task on the board
                    if (listCount() <= 0) return;
                    var lastList = getTaskListModelsSorted().get(listCount());
                    if (lastList.taskCount() <= 0) return;

                    // navigate to the last task on the board
                    lastList.getSortedTasks().get(lastList.taskCount()).triggerFocus();
                    return;
                }
                var nextList = taskListModelsSorted.get(listIndex + 1);
                if (nextList.taskCount() <= 0) return;

                var nextTask = nextList.getSortedTasks().get(Math.min(taskIndexInList, nextList.taskCount()));
                nextTask.triggerFocus();
            }
            case UP -> {
                if (taskIndexInList <= 1) { // If we are at the first task in the list, we should navigate to the previous list's last task
                    if (listIndex <= 1) {
                        return;
                    }
                    var prevList = taskListModelsSorted.get(listIndex - 1);
                    var prevTask = prevList.getSortedTasks().get(prevList.getSortedTasks().size() - 2);
                    prevTask.triggerFocus();
                } else {
                    var taskUp = taskListModelsSorted.get(listIndex).getSortedTasks().get(taskIndexInList - 1);
                    taskUp.triggerFocus();
                }
            }
            case DOWN -> {
                if (taskIndexInList >= taskListModelsSorted.get(listIndex).taskCount()) { // If we are at the last task in the list, we should navigate to the next list's first task
                    if (listIndex >= listCount()) {
                        return;
                    }
                    var nextList = taskListModelsSorted.get(listIndex + 1);
                    var nextTask = nextList.getSortedTasks().get(1);
                    nextTask.triggerFocus();
                } else {
                    var taskDown = taskListModelsSorted.get(listIndex).getSortedTasks().get(taskIndexInList + 1);
                    taskDown.triggerFocus();
                }
            }


        }
    }

    /**
     * Moves the task to the direction specified, from the given list and task index.
     * UP/DOWN will move the task in the list, LEFT/RIGHT will move the task to the next list's end when possible.
     * If the move wouldn't be possible, it doesn't do anything, doesn't even set the task to be focused.
     *
     * @param direction       the direction in which the card should be moved
     * @param listIndex       the index of the list that contains the source task. 1 is the first list.
     * @param taskIndexInList the index of the task in the task list. 1 is the first task.
     *                                                                                                                                                                                                                todo add tests
     */
    public void moveTaskInDirection(NavigationDirection direction, int listIndex, int taskIndexInList) {
        var sourceList = taskListModelsSorted.get(listIndex);
        switch (direction) {
            case DOWN -> {
                if (taskIndexInList >= sourceList.taskCount()) return;
                var sourceTask = sourceList.getSortedTasks().get(taskIndexInList);
                sourceList.moveInTaskAfter(sourceTask, sourceList.getSortedTasks().get(taskIndexInList + 1));
            }
            case UP -> {
                if (taskIndexInList <= 1) return;
                var sourceTask = sourceList.getSortedTasks().get(taskIndexInList);
                sourceList.moveInTaskBefore(sourceTask, sourceList.getSortedTasks().get(taskIndexInList - 1));
            }
            case LEFT -> {
                if (listIndex <= 1) return;
                var sourceTask = sourceList.getSortedTasks().get(taskIndexInList);
                var targetList = taskListModelsSorted.get(listIndex - 1);
                targetList.moveInTaskBefore(sourceTask, targetList.getLastDummy());
            }
            case RIGHT -> {
                if (listIndex >= listCount()) return;
                var sourceTask = sourceList.getSortedTasks().get(taskIndexInList);
                var targetList = taskListModelsSorted.get(listIndex + 1);
                targetList.moveInTaskBefore(sourceTask, targetList.getLastDummy());
            }
        }
    }

    int listCount() {
        return taskListModelsSorted.size() - 2;
    }

    public TagModel getTagById(UUID id) {
        return tagModels.stream().filter(tagModel -> tagModel.getId().equals(id)).findFirst().orElseThrow();
    }

    public void peerAddedTag(Tag tag) {
        tagModels.add(tagModelFactory.create(tag, this));
    }

    public void peerUpdatedTag(Tag tag) {
        var tagModel = getTagById(tag.getId());
        tagModel.peerUpdated(tag);
    }

    public void peerAddedTagToTask(UUID taskId, UUID tagId) {
        var taskModel = getTaskById(taskId);
        var tagModel = getTagById(tagId);
        taskModel.peerAddedTag(tagModel);
    }

    public TaskModel getTaskById(UUID taskId) {
        for (var taskListModel : taskListModelsSorted) {
            for (var taskModel : taskListModel.getSortedTasks()) {
                if (taskModel.getId().equals(taskId)) {
                    return taskModel;
                }
            }
        }
        throw new NoSuchElementException("Task with id " + taskId + " not found");
    }

    public void peerRemovedTagFromTask(UUID taskId, UUID tagId) {
        var taskModel = getTaskById(taskId);
        var tagModel = getTagById(tagId);
        taskModel.peerRemovedTag(tagModel);
    }

    public void peerCreatedSubtask(UUID parentTaskId, SubTask createdSubTask) {
        var parentTask = getTaskById(parentTaskId);
        parentTask.peerCreatedSubtask(createdSubTask);
    }

    public void peerUpdatedSubtask(SubTask subtask) {
        var subtaskModel = getSubtaskById(subtask.getId());
        if (subtaskModel.isEmpty()) {
            throw new NoSuchElementException("Subtask with id " + subtask.getId() + " not found");
        }
        subtaskModel.get().peerUpdated(subtask);
    }

    public Optional<SubTaskModel> getSubtaskById(UUID id) {
        for (var taskListModel : taskListModelsSorted) {
            for (var taskModel : taskListModel.getSortedTasks()) {
                for (var subTaskModel : taskModel.getSubtasks()) {
                    if (subTaskModel.getId().equals(id)) {
                        return Optional.of(subTaskModel);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void peerSwappedSubtaskPosition(UUID subtaskId1, UUID subtaskId2) {
        var subtask1 = getSubtaskById(subtaskId1);
        var subtask2 = getSubtaskById(subtaskId2);
        if (subtask1.isEmpty() || subtask2.isEmpty()) {
            throw new NoSuchElementException("Subtask with id " + subtaskId1 + " or " + subtaskId2 + " not found");
        }
        var parentTask = subtask1.get().getParentTask();
        parentTask.peerSwappedSubtaskPosition(subtask1.get(), subtask2.get());
    }

    public void peerRemovedSubtask(UUID removedSubtaskId) {
        var removedSubtask = getSubtaskById(removedSubtaskId);
        if (removedSubtask.isEmpty()) {
            throw new NoSuchElementException("Subtask with id " + removedSubtaskId + " not found");
        }
        var parentTask = removedSubtask.get().getParentTask();
        parentTask.peerRemovedSubtask(removedSubtask.get());
    }

    public void removeHighlight(HighlightModel selectedItem) {
        highlightModels.remove(selectedItem);
        for( var taskList : taskListModelsSorted) {
            for (var task : taskList.getSortedTasks()) {
                task.setHighlight(defaultHighlight.get());
            }
        }
        sessionContext.removeHighlight(selectedItem.getId());
    }

    public HighlightModel getDefaultHighlight() {
        return defaultHighlight.get();
    }

    public ObjectProperty<HighlightModel> defaultHighlightProperty() {
        return defaultHighlight;
    }

    public Optional<HighlightModel> getHighlightById(UUID taskHighlightId) {
        return highlightModels.stream()
                .filter(highlightModel -> highlightModel.getId().equals(taskHighlightId))
                .findFirst();
    }

    public void peerCreatedHighlight(TaskHighlight highlight) {
        highlightModels.add(new HighlightModel(highlight, this, sessionContext));
    }

    public void peerUpdatedHighlight(TaskHighlight highlight) {
        var highlightModel = getHighlightById(highlight.getId());
        if (highlightModel.isEmpty()) {
            throw new NoSuchElementException("Highlight with id " + highlight.getId() + " not found");
        }
        highlightModel.get().peerUpdated(highlight);
    }

    public void peerRemovedHighlight(UUID removedHighlightId) {
        var removedHighlight = getHighlightById(removedHighlightId);
        if (removedHighlight.isEmpty()) {
            throw new NoSuchElementException("Highlight with id " + removedHighlightId + " not found");
        }
        highlightModels.remove(removedHighlight.get());
    }

    public enum NavigationDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public interface Factory {
        BoardModel create(Board serverBoard);

        BoardModel create(UUID id);
    }
}
