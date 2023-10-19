package client.components;

import com.google.inject.Inject;
import commons.utils.SmartColor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;

public class AddButton extends Button {
    private final ObjectProperty<SmartColor> foregroundColor = new SimpleObjectProperty<>(new SmartColor(0, 0, 0, 1));
    private final ObjectProperty<SmartColor> backgroundColor = new SimpleObjectProperty<>(new SmartColor(1, 1, 1, 1));
    private final ObservableValue<SmartColor> hoverBackgroundColor;
    private final ObservableValue<SmartColor> hoverTextColor;
    private final ObjectProperty<SmartColor> currentBackground;
    private final ObjectProperty<SmartColor> currentTextColor;

    @Inject
    public AddButton(ColorGenerator colorGenerator) {
        getStyleClass().add("AddButton");

        setText("+ Add Card");
        this.setStyle("-fx-background-color: " + getBackgroundColor());

        hoverBackgroundColor = backgroundColor.map(colorGenerator::calculateHoverBackgroundColor);
        hoverTextColor = Bindings.createObjectBinding(() -> colorGenerator.calculateHoverForegroundColor(foregroundColor.get(), backgroundColor.get()), foregroundColor, backgroundColor);

        currentBackground = new SimpleObjectProperty<>();
        currentBackground.bind(backgroundColor);

        currentTextColor = new SimpleObjectProperty<>();
        currentTextColor.bind(foregroundColor);

        initElements();
    }

    public interface Factory {
        AddButton create();
    }
    void initElements() {
        this.setOnMouseEntered(event -> {
            currentBackground.bind(hoverBackgroundColor);
            currentTextColor.bind(hoverTextColor);
            setTextFill(hoverTextColor.getValue().toFXColor());
        });
        this.setOnMouseExited(event -> {
            currentBackground.bind(backgroundColor);
            currentTextColor.bind(foregroundColor);
            setTextFill(getForegroundColor().toFXColor());
        });

        // update view
        currentBackground.addListener(o -> this.setStyle("-fx-background-color: " + currentBackground.getValue()));
        currentTextColor.addListener(o -> this.setTextFill(currentTextColor.get().toFXColor()));
    }
    

    public SmartColor getForegroundColor() {
        return foregroundColor.get();
    }

    public void setForegroundColor(SmartColor c) {
        foregroundColor.set(c);
    }

    public ObjectProperty<SmartColor> foregroundColorProperty() {
        return foregroundColor;
    }

    public SmartColor getBackgroundColor() {
        return backgroundColor.get();
    }

    public void setBackgroundColor(SmartColor c) {
        backgroundColor.set(c);
    }


    public ObjectProperty<SmartColor> backgroundColorProperty() {
        return backgroundColor;
    }

}
