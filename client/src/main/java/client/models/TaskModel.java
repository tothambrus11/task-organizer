package client.models;

import client.contexts.SessionContext;
import client.utils.BigFractionProperty;
import client.utils.UUIDProperty;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.models.SubTask;
import commons.models.Task;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Objects;
import java.util.UUID;

public class TaskModel implements Comparable<TaskModel> {
    // Entity properties
    private final UUIDProperty id;
    private final ObjectProperty<TaskListModel> parentTaskList;
    private final BigFractionProperty position = new BigFractionProperty(BigFraction.ONE_HALF);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    // Sub entities
    private final ObservableList<SubTaskModel> subtasks = FXCollections.observableArrayList();
    private final SortedList<SubTaskModel> subtasksSorted = new SortedList<>(subtasks, SubTaskModel::compareTo);
    private final ObservableList<TagModel> tags = FXCollections.observableArrayList();
    private final SortedList<TagModel> sortedTags = new SortedList<>(tags, (o1, o2) -> Objects.compare(o1.getName(), o2.getName(), String::compareTo));
    private final ObjectProperty<HighlightModel> highlight = new SimpleObjectProperty<>();

    // Dependencies
    private final SessionContext sessionContext;
    private final SubTaskModel.Factory subtaskModelFactory;
    private Runnable requestFocusCallback = null;
    // Frontend Model state
    private Runnable onDelete = () -> {
    };

    /**
     * Creates a new task with a random id and a given list id
     *
     * @param parentTaskList task list this task belongs to
     */
    @AssistedInject
    private TaskModel(@Assisted TaskListModel parentTaskList,
                      SessionContext sessionContext,
                      SubTaskModel.Factory subTaskModelFactory) {

        this(parentTaskList, UUID.randomUUID(), sessionContext, subTaskModelFactory);
    }

    /**
     * Creates a new task with a given id and list id
     *
     * @param parentTaskList task list this task belongs to
     * @param id             key of this task
     */
    @AssistedInject
    private TaskModel(@Assisted TaskListModel parentTaskList,
                      @Assisted UUID id,
                      SessionContext sessionContext,
                      SubTaskModel.Factory subTaskModelFactory) {
        this.parentTaskList = new SimpleObjectProperty<>(parentTaskList);
        this.id = new UUIDProperty(id);
        this.sessionContext = sessionContext;
        this.subtaskModelFactory = subTaskModelFactory;
    }

    @AssistedInject
    private TaskModel(@Assisted TaskListModel parentTaskList,
                      @Assisted Task serverTask,
                      SessionContext sessionContext,
                      SubTaskModel.Factory subTaskModelFactory) {
        this(parentTaskList, serverTask.getId(), sessionContext, subTaskModelFactory);
        setTitle(serverTask.getTitle());
        setDescription(serverTask.getDescription());
        setPosition(serverTask.getPosition());
        System.out.println("Loading subtasks... " + serverTask.getSubTasks().size() + " to be loaded!");
        for (var serverSubTask : serverTask.getSubTasks()) {
            subtasks.add(subTaskModelFactory.create(this, serverSubTask));
        }

        // init tags
        serverTask.getTags().forEach(tag -> {
            var tagModel = parentTaskList.getParentBoard().getTagById(tag);
            tags.add(tagModel);
        });

        // init highlight
        var highlight = parentTaskList.getParentBoard().getHighlightById(serverTask.getTaskHighlightId());
        if (highlight.isPresent()) {
            setHighlight(highlight.get());
        } else {
            setHighlight(parentTaskList.getParentBoard().getDefaultHighlight());
        }
    }

    public Runnable getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;
    }

    public Runnable getRequestFocusCallback() {
        return requestFocusCallback;
    }

    public void setRequestFocusCallback(Runnable requestFocusCallback) {
        this.requestFocusCallback = requestFocusCallback;
    }

    @Override
    public int compareTo(TaskModel o) {
        return position.get().compareTo(o.position.get());
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

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public HighlightModel getHighlight() {
        return highlight.get();
    }

    public void setHighlight(HighlightModel highlight) {
        this.highlight.set(highlight);
    }

    public ObjectProperty<HighlightModel> highlightProperty() {
        return highlight;
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

    public TaskListModel getParentTaskList() {
        return parentTaskList.get();
    }

    public void setParentTaskList(TaskListModel parentTaskList) {
        this.parentTaskList.set(parentTaskList);
    }

    public ObjectProperty<TaskListModel> parentTaskListProperty() {
        return parentTaskList;
    }

    public String getDescription() {
        return description.get();
    }


    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<SubTaskModel> getSubtasks() {
        return subtasksSorted;
    }

    public boolean isDummy() {
        return false;
    }

    public Task toServerTask() {
        var serverTask = new Task();
        serverTask.setId(getId());
        serverTask.setTitle(getTitle());
        serverTask.setDescription(getDescription());
        serverTask.setPosition(getPosition());
        serverTask.setTaskListId(getParentTaskList().getId());
        serverTask.setTaskHighlightId(getHighlight() == null ? null : getHighlight().getId());
        return serverTask;
    }

    public void save() {
        sessionContext.updateTask(toServerTask());
    }

    public void peerUpdatedTask(Task task) {
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setPosition(task.getPosition());
        setHighlight(parentTaskList.get().getParentBoard().getHighlightById(task.getTaskHighlightId()).orElse(parentTaskList.get().getParentBoard().getDefaultHighlight()));
    }

    public void remove() {
        getParentTaskList().removeTask(this);
    }

    /**
     * Requests focus on the task. The requestFocusCallback should be set by the view before calling this method.
     */
    public void triggerFocus() {
        if (requestFocusCallback != null) {
            requestFocusCallback.run();
        }
    }

    /**
     * Creates a new subtask at the end of the list with position 1 higher than the highest position. If there are no
     * subtasks, the first subtask will have position 1.
     */
    public void createSubTaskAtTheEnd() {
        var subTask = subtaskModelFactory.create(this);
        subTask.setPosition(subtasks.stream()
                .map(SubTaskModel::getPosition)
                .max(Long::compareTo)
                .orElse(0L) + 1);
        subtasks.add(subTask);
        System.out.println("Creating a subtask!");
        sessionContext.createSubTask(getId(), subTask.toServerSubTask());
    }

    public void deleteSubtask(SubTaskModel subtask) {
        this.subtasks.remove(subtask);
        this.sessionContext.removeSubtask(subtask.getId());
    }

    /**
     * Returns the sorted list of tags of this task. It is not allowed to modify the list using this directly.
     * {@see addTag}, {@see removeTag}
     */
    public SortedList<TagModel> getTags() {
        return sortedTags;
    }

    /**
     * Adds an already existing tag to this task and notifies the server.
     */
    public void addTag(TagModel tag) {
        tags.add(tag);
        sessionContext.addTagToTask(getId(), tag.getId());
    }

    /**
     * Removes an already existing tag from this task and notifies the server. The tag won't be deleted from the board.
     */
    public void removeTag(TagModel tag) {
        tags.remove(tag);
        sessionContext.removeTagFromTask(getId(), tag.getId());
    }

    /**
     * Called when a tag is deleted from the board. This method removes the tag from this task locally. The server will
     * be already notified of the deletion by the {@see board.deleteTag} method.
     *
     * @param removedTagId the id of the tag that was deleted
     */
    public void onTagDeleted(UUID removedTagId) {
        tags.removeIf(t -> Objects.equals(removedTagId, t.getId()));
    }

    public void peerAddedTag(TagModel tagModel) {
        tags.add(tagModel);
    }

    public void peerRemovedTag(TagModel tagModel) {
        tags.remove(tagModel);
    }

    public void moveSubtaskUp(SubTaskModel subTaskModel) {
        var index = subtasksSorted.indexOf(subTaskModel);
        if (index > 0) {
            swapPosition(subtasksSorted.get(index - 1), subTaskModel);
        }
    }

    public void moveSubtaskDown(SubTaskModel subTaskModel) {
        var index = subtasksSorted.indexOf(subTaskModel);
        if (index < subtasksSorted.size() - 1) {
            swapPosition(subtasksSorted.get(index + 1), subTaskModel);
        }
    }

    public void swapPosition(SubTaskModel a, SubTaskModel b) {
        peerSwappedSubtaskPosition(a, b);
        sessionContext.swapSubtaskPosition(a.getId(), b.getId());
    }

    public void peerCreatedSubtask(SubTask createdSubTask) {
        var subTask = subtaskModelFactory.create(this, createdSubTask);
        subtasks.add(subTask);
    }

    public void peerSwappedSubtaskPosition(SubTaskModel a, SubTaskModel b) {
        var tempPos = a.getPosition();
        a.setPosition(b.getPosition());
        b.setPosition(tempPos);
        subtasks.removeAll(a, b);
        subtasks.addAll(a, b);
    }

    public void peerRemovedSubtask(SubTaskModel removedSubtask) {
        subtasks.remove(removedSubtask);
    }

    public interface Factory {
        TaskModel create(TaskListModel parentTaskList);

        TaskModel create(TaskListModel parentTaskList, UUID id);

        TaskModel create(TaskListModel parentTaskList, Task serverTask);
    }

    public static class DummyTask extends TaskModel {
        @Inject
        private DummyTask(@Assisted TaskListModel parentTaskList,
                          @Assisted BigFraction position,
                          SessionContext sessionContext,
                          SubTaskModel.Factory subTaskModelFactory) {
            super(parentTaskList, sessionContext, subTaskModelFactory);
            this.setPosition(position);
        }

        @Override
        public boolean isDummy() {
            return true;
        }

        public interface Factory {
            DummyTask create(TaskListModel parentTaskList, BigFraction position);
        }
    }

}
