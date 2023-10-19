package client.models;


import client.contexts.SessionContext;
import client.utils.UUIDProperty;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.models.SubTask;
import javafx.beans.property.*;

import java.util.UUID;

public class SubTaskModel implements Comparable<SubTaskModel> {

    private final UUIDProperty id;

    private final TaskModel parentTask;

    private final LongProperty position = new SimpleLongProperty(-1);

    private final StringProperty name = new SimpleStringProperty("");

    private final BooleanProperty completed = new SimpleBooleanProperty(false);

    private final SessionContext sessionContext;

    @AssistedInject
    public SubTaskModel(@Assisted TaskModel parentTask, SessionContext sessionContext) {
        this(parentTask, UUID.randomUUID(), sessionContext);
    }

    @AssistedInject
    public SubTaskModel(@Assisted TaskModel parentTask, @Assisted UUID id, SessionContext sessionContext) {
        this.parentTask = parentTask;
        this.id = new UUIDProperty(id);
        this.sessionContext = sessionContext;
    }

    @AssistedInject
    public SubTaskModel(@Assisted TaskModel parentTask, @Assisted SubTask serverSubTask, SessionContext sessionContext) {
        this(parentTask, serverSubTask.getId(), sessionContext);
        setName(serverSubTask.getTitle());
        setPosition(serverSubTask.getPosition());
        setCompleted(serverSubTask.getCompleted());
    }

    @Override
    public int compareTo(SubTaskModel o) {
        return Long.compare(position.get(), o.position.get());
    }

    public UUID getId() {
        return id.getValue();
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public UUIDProperty idProperty() {
        return id;
    }

    public Long getPosition() {
        return position.get();
    }

    public void setPosition(Long position) {
        this.position.setValue(position);
    }

    public void setPosition(long position) {
        this.position.set(position);
    }

    public LongProperty positionProperty() {
        return position;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.setValue(completed);
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    /**
     * Saves the task to the server
     */
    public void save() {
        sessionContext.updateSubTask(toServerSubTask());
    }

    /**
     * Deletes the task from the server
     */
    public void delete() {
        parentTask.deleteSubtask(this);
    }

    public TaskModel getParentTask() {
        return parentTask;
    }

    public SubTask toServerSubTask() {
        var serverSubTask = new SubTask();
        serverSubTask.setId(id.get());
        serverSubTask.setTitle(getName());
        serverSubTask.setPosition(getPosition());
        serverSubTask.setCompleted(isCompleted());
        return serverSubTask;
    }

    public void moveUp() {
        parentTask.moveSubtaskUp(this);
    }

    public void moveDown() {
        parentTask.moveSubtaskDown(this);
    }

    public void peerUpdated(SubTask subtask) {
        setName(subtask.getTitle());
        setPosition(subtask.getPosition());
        setCompleted(subtask.getCompleted());
    }

    public interface Factory {
        SubTaskModel create(TaskModel parentTask);

        SubTaskModel create(TaskModel parentTask, UUID id);

        SubTaskModel create(TaskModel parentTask, SubTask serverSubTask);
    }
}

