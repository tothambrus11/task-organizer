package client.components;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class PopUp extends VBox {

    SimpleBooleanProperty shown;

    SimpleBooleanProperty displayOverlay;

    HBox container;


    public PopUp() {

        super();

        //Setting default values
        shown = new SimpleBooleanProperty(false);

        displayOverlay = new SimpleBooleanProperty(true);

        //Creating a popup element
        container = new HBox();

        this.setId("popUpV");
        container.setId("popUpH");

        this.getChildren().add(container);

        this.setFocusTraversable(true);

        this.setAlignment(Pos.CENTER);

        container.setAlignment(Pos.CENTER);

        //Adding a style class
        this.getStyleClass().add("PopUp");
        this.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        //Setting all the triggers to close the popup, such as clicking outside and escape button
        this.setOnMouseClicked(e -> {
            if (Objects.equals(e.getPickResult().getIntersectedNode().getId(), "popUpH") || Objects.equals(e.getPickResult().getIntersectedNode().getId(), "popUpV"))
                this.close();
        });

        this.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) this.close();
        });

        this.displayOverlayProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("-fx-background-color: rgba(0,0,0,0.3);");
            }
            else {
                setStyle("-fx-background-color: transparent;");
            }
        }));

        //Binding visibility value of the component to the
        this.visibleProperty().bindBidirectional(shown);

    }


    public void open() {
        this.shown.setValue(true);
        this.setViewOrder(-1000);
        this.requestFocus();
    }


    public void close() {
        this.shown.setValue(false);
    }


    public void toggle() {
        this.shown.setValue(!shown.get());
    }


    public SimpleBooleanProperty shownProperty() {
        return shown;
    }


    public boolean getShown() {
        return shown.get();
    }


    public void setShown(boolean value) {
        shown.set(value);
    }


    public void setContent(Node content) {
        if (container.getChildren().size() == 0) container.getChildren().add(content);
        container.getChildren().set(0, content);
    }


    public Node getContent() {
        if (container.getChildren().size() == 0) return null;
        return container.getChildren().get(0);
    }


    public void setDisplayOverlay(boolean value) {
        this.displayOverlay.set(value);

    }

    public SimpleBooleanProperty displayOverlayProperty() {
        return this.displayOverlay;
    }


}
