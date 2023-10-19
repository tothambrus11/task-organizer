package client.models;

import commons.models.BoardInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.UUID;


public class BoardPreviewModel {

    //    private final ObservableList<BoardPreviewModel> observableBoardPreviewList;
    private final UUID id;
    private final StringProperty joinKey = new SimpleStringProperty();
    private final WorkspaceModel parentWorkspace;
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty creator = new SimpleStringProperty();
    private LocalDateTime lastAccessTime;
    private int viewOrder;
    public BoardPreviewModel(WorkspaceModel workspaceModel) {
        this(workspaceModel, UUID.randomUUID());
    }
    /**
     * Creates a new task with a given id and list id
     *
     * @param workspaceModel task list this task belongs to
     * @param id             key of this task
     */
    public BoardPreviewModel(WorkspaceModel workspaceModel, UUID id) {
        this.parentWorkspace = workspaceModel;
        this.id = id;
    }
    public BoardPreviewModel(WorkspaceModel wsModel, BoardInfo info) {
        this(wsModel, info.getId());
        setTitle(info.getTitle());
        setCreator(info.getCreator());
        setLastAccessTime(info.getLastJoinTime());
        setJoinKey(info.getJoinKey());
    }

    public String getJoinKey() {
        return joinKey.get();
    }

    public void setJoinKey(String joinKey) {
        this.joinKey.set(joinKey);
    }

    public StringProperty joinKeyProperty() {
        return joinKey;
    }

    public void delete() {
        parentWorkspace.deleteBoardPreview(this);
    }

    public void leave() {
        parentWorkspace.leaveBoardPreview(this);
    }


    public UUID getId() {
        return id;
    }

    public WorkspaceModel getParentWorkspace() {
        return parentWorkspace;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
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

    public String getCreator() {
        return creator.get();
    }

    public void setCreator(String creator) {
        this.creator.set(creator);
    }

    public StringProperty creatorProperty() {
        return creator;
    }

    public int getViewOrder() {
        return viewOrder;
    }

    public void setViewOrder(int viewOrder) {
        this.viewOrder = viewOrder;
    }


}
