package client.components;

import client.SimpleFXMLLoader;
import commons.utils.SmartColor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class RoundColorPicker extends AnchorPane {

    @FXML
    private Circle colorCircle;
    @FXML
    private ColorPicker colorPicker;

    private ObjectProperty<SmartColor> color = new SimpleObjectProperty<>(new SmartColor(1.0, 1.0, 1.0, 0));
    private DropShadow shadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.25));
    public RoundColorPicker() {
        getStyleClass().add("round-color-picker");
        SimpleFXMLLoader._initView("/client/components/RoundColorPicker.fxml", this);

        colorPicker.setFocusTraversable(false);
        colorCircle.setFocusTraversable(true);
        colorCircle.setEffect(shadow);

        colorPicker.setOnAction(event -> {
            var c = SmartColor.valueOf(colorPicker.getValue());
            System.out.println("Color: " + c);
            color.set(c);
        });

        colorCircle.setOnMouseClicked(event -> {
            colorPicker.show();
            colorCircle.requestFocus();
        });

        colorCircle.setOnKeyPressed(event ->{
            if(event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER){
                colorPicker.show();
            }
        });

        color.addListener((v, oldValue, newValue) -> {
            updateView(newValue);
        });

        setPrefSize(24, 24);
        setMaxSize(24,24);
        setMinSize(24,24);
    }

    private void updateView(SmartColor color) {
        colorCircle.setFill(color.toFXColor());
        colorPicker.setValue(color.toFXColor());
    }

    public SmartColor getColor() {
        return color.get();
    }

    public ObjectProperty<SmartColor> colorProperty() {
        return color;
    }

    public void setColor(SmartColor color) {
        this.color.set(color);
    }
}

