package client.models;

import client.contexts.SessionContext;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.models.TaskHighlight;
import commons.utils.SmartColor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class HighlightModel {
    private final ObjectProperty<SmartColor> foregroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<SmartColor> backgroundColor = new SimpleObjectProperty<>();
    private final BoardModel board;
    private final SessionContext sessionContext;
    private StringProperty name = new SimpleStringProperty();
    private UUID id;
    private Long position;

    @AssistedInject
    public HighlightModel(@Assisted UUID id,
                          @Assisted String name,
                          @Assisted SmartColor foregroundColor,
                          @Assisted SmartColor backgroundColor,
                          @Assisted BoardModel parentBoard,
                          @Assisted Long position,
                          SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.board = parentBoard;
        this.id = id;
        this.name.set(name);
        this.foregroundColor.set(foregroundColor);
        this.backgroundColor.set(backgroundColor);
        this.position = position;
    }

    @AssistedInject
    public HighlightModel(@Assisted TaskHighlight serverHighlight, @Assisted BoardModel parentBoard, SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.board = parentBoard;
        this.id = serverHighlight.getId();
        this.name.set(serverHighlight.getName());
        this.foregroundColor.set(serverHighlight.getForegroundColor());
        this.backgroundColor.set(serverHighlight.getBackgroundColor());
        this.position = serverHighlight.getPosition();
    }

    public static int compareTo(HighlightModel a, HighlightModel b) {
        return Long.compare(a.getPosition(), b.getPosition());
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public TaskHighlight toServerHighlight() {
        return new TaskHighlight(id, name.get(), foregroundColor.get(), backgroundColor.get(), position);
    }

    public BoardModel getBoard() {
        return board;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        System.out.println("Setting name to " + name);
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public SmartColor getForegroundColor() {
        return foregroundColor.get();
    }

    public void setForegroundColor(SmartColor foregroundColor) {
        System.out.println("Setting foreground color to " + foregroundColor);
        this.foregroundColor.set(foregroundColor);
    }

    public ObjectProperty<SmartColor> foregroundColorProperty() {
        return foregroundColor;
    }

    public SmartColor getBackgroundColor() {
        return backgroundColor.get();
    }

    public void setBackgroundColor(SmartColor backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    public ObjectProperty<SmartColor> backgroundColorProperty() {
        return backgroundColor;
    }

    public void save() {
        sessionContext.saveHighlight(toServerHighlight());
    }

    public void delete() {
        sessionContext.removeHighlight(getId());
    }

    public void peerUpdated(TaskHighlight highlight) {
        setName(highlight.getName());
        setForegroundColor(highlight.getForegroundColor());
        setBackgroundColor(highlight.getBackgroundColor());
        setPosition(highlight.getPosition());
    }


//    @Override
//    public String toString() {
//        return this.getName();
//    }
}
