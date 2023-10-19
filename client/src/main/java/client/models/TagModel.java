package client.models;

import client.contexts.SessionContext;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.models.Tag;
import commons.utils.SmartColor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class TagModel {
    private final UUID id;
    private final BoardModel parentBoard;
    private final StringProperty name = new SimpleStringProperty("");
    private final ObjectProperty<SmartColor> color = new SimpleObjectProperty<>(new SmartColor(0.0, 1.0, 0.0, 1.0));
    private final SessionContext sessionContext;


    @AssistedInject
    public TagModel(@Assisted UUID id, @Assisted BoardModel parentBoard, @Assisted String name, @Assisted SmartColor color, SessionContext sessionContext) {
        this.id = id;
        this.parentBoard = parentBoard;
        this.sessionContext = sessionContext;
        this.name.set(name);
        this.color.set(color);

        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Tag name cannot be empty");
    }

    @AssistedInject
    public TagModel(@Assisted Tag serverTag, @Assisted BoardModel parentBoard, SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.parentBoard = parentBoard;
        this.id = serverTag.getId();
        if (serverTag.getName() == null || serverTag.getName().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null nor empty");
        }
        if (serverTag.getColor() == null) {
            throw new IllegalArgumentException("Tag color cannot be null");
        }
        setName(serverTag.getName());
        setColor(serverTag.getColor());
    }

    public UUID getId() {
        return id;
    }

    public BoardModel getParentBoard() {
        return parentBoard;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public SmartColor getColor() {
        return color.get();
    }

    public void setColor(SmartColor color) {
        this.color.set(color);
    }

    public ObjectProperty<SmartColor> colorProperty() {
        return color;
    }

    public Tag toServerTag() {
        var serverTag = new Tag();
        serverTag.setId(getId());
        serverTag.setName(getName());
        serverTag.setColor(getColor());
        serverTag.setBoardId(getParentBoard().getId());
        return serverTag;
    }

    public void save() {
        sessionContext.updateTag(toServerTag());
    }

    /**
     * Removes this tag from the board and notifies the server.
     */
    public void remove() {
        getParentBoard().deleteTag(this);
    }

    public void peerUpdated(Tag tag) {
        setColor(tag.getColor());
        setName(tag.getName());
    }

    public interface Factory {
        TagModel create(UUID id, BoardModel parentBoard, String name, SmartColor color);

        TagModel create(Tag serverTag, BoardModel parentBoard);
    }
}

