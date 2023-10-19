package client.components;


import commons.utils.SmartColor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class IconButton extends Button {
    private final ColorGenerator colorGenerator = new ColorGenerator(); // todo add dependency injection
    private final ObjectProperty<SmartColor> foregroundColor = new SimpleObjectProperty<>(new SmartColor(0, 0, 0, 1));
    private final SimpleObjectProperty<SmartColor> backgroundColor = new SimpleObjectProperty<>(new SmartColor(1, 1, 1, 1));
    private final ObservableValue<SmartColor> hoverBackgroundColor;
    private final ObservableValue<SmartColor> hoverForegroundColor;
    private final ObjectProperty<SmartColor> currentBackground;
    private final ObjectProperty<SmartColor> currentForeground;
    private final StringProperty source = new SimpleStringProperty("/client/icons/transparent.png");
    private final ObjectProperty<ButtonSize> size = new SimpleObjectProperty<>(ButtonSize.SMALL);
    @FXML
    private Pane backgroundColorPane;
    @FXML
    private Pane foregroundColorPane;
    @FXML
    private Pane invertedContainer;

    public void setVisibility(boolean visible) {
        backgroundColorPane.setVisible(visible);
        foregroundColorPane.setVisible(visible);
        invertedContainer.setVisible(visible);
    }

    public IconButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/components/IconButton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        getStyleClass().add("IconButton");

        hoverBackgroundColor = backgroundColor.map(colorGenerator::calculateHoverBackgroundColor);
        hoverForegroundColor = Bindings.createObjectBinding(() -> colorGenerator.calculateHoverForegroundColor(foregroundColor.get(), backgroundColor.get()), foregroundColor, backgroundColor);


        currentBackground = new SimpleObjectProperty<>();
        currentBackground.bind(backgroundColor);

        currentForeground = new SimpleObjectProperty<>();
        currentForeground.bind(foregroundColor);

        initListeners();
    }

    private void initListeners() {
        hoverProperty().addListener(o -> {
            if (isHover()) {
                currentForeground.bind(hoverForegroundColor);
                currentBackground.bind(hoverBackgroundColor);
            } else {
                currentForeground.bind(foregroundColor);
                currentBackground.bind(backgroundColor);
            }
        });

        backgroundColorPane.styleProperty().bind(currentBackground.map(c -> "-fx-background-radius: 3px; -fx-background-color: " + c));
        foregroundColorPane.styleProperty().bind(currentForeground.map(c -> "-fx-background-radius: 3px; -fx-background-color: " + c));
    }

    public SmartColor getForegroundColor() {
        return foregroundColor.get();
    }

    public void setForegroundColor(SmartColor foregroundColor) {
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

    public SimpleObjectProperty<SmartColor> backgroundColorProperty() {
        return backgroundColor;
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public String getSource() {
        return source.get();
    }

    public void setSource(String source) {
        this.source.set(source);

        Image img = new Image(source);

        // updating first image
        backgroundColorPane.getChildren().clear();
        ImageView imageViewNormal = new ImageView(img);
        updateImageSize(imageViewNormal);
        imageViewNormal.setBlendMode(BlendMode.MULTIPLY);
        backgroundColorPane.getChildren().add(imageViewNormal);

        // updating the inverted image
        invertedContainer.getChildren().clear();
        ImageView imageViewInverted = new ImageView(img);
        updateImageSize(imageViewInverted);
        imageViewInverted.setBlendMode(BlendMode.DIFFERENCE);
        invertedContainer.getChildren().add(imageViewInverted);
    }

    public ButtonSize getSize() {
        return size.get();
    }

    public void setSize(ButtonSize s) {
        size.set(s);
    }


    private void updateImageSize(ImageView image) {
        var height = switch (this.size.get()) {
            case EXTRA_SMALL -> 13.4;
            case SMALL -> 24;
            case LARGE -> 36;
        };
        var width = switch (this.size.get()) {
            case SMALL, EXTRA_SMALL -> 24;
            case LARGE -> 36;
        };
        this.setPrefSize(width, height);
        this.setMinSize(width, height);
        image.setFitHeight(height);
        image.setFitWidth(width);
    }

    public enum ButtonSize {EXTRA_SMALL, SMALL, LARGE}

}
