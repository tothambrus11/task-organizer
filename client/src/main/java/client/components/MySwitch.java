package client.components;

import client.SimpleFXMLLoader;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MySwitch extends AnchorPane {
    @FXML
    private Rectangle background;
    @FXML
    private VBox knob;

    SimpleBooleanProperty state = new SimpleBooleanProperty(false);

    TranslateTransition translation;

    public MySwitch() {
        SimpleFXMLLoader._initView("/client/components/MySwitch.fxml", this);
        getStyleClass().add("MySwitch");

        translation = new TranslateTransition(Duration.millis(200), knob);

        background.onMouseClickedProperty().set(event -> switchState());
        knob.onMouseClickedProperty().set(event -> switchState());
        knob.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER, SPACE -> switchState();
            }
        });

        state.addListener((e) -> {
            if (state.get()) {
                translation.setToX(40 - 18);
                background.setFill(Paint.valueOf("#0C8CE9"));
            } else {
                translation.setToX(0);
                background.setFill(Paint.valueOf("#d9d9d9"));
            }
            translation.play();
        });
    }

    public void setState(boolean value) {
        state.set(value);
    }

    public void switchState() {
        state.set(!state.get());
    }

    public boolean getState() {
        return state.get();
    }

    public SimpleBooleanProperty stateProperty() {
        return state;
    }
}
