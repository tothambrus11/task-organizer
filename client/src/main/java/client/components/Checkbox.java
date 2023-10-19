package client.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Checkbox extends ImageView {

    private final BooleanProperty checked = new SimpleBooleanProperty(false);

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public Checkbox(){
        super();
        InputStream stream = this.getClass().getResourceAsStream("/client/images/CheckmarkOff.png");

        assert stream != null;
        Image image = new Image(stream);
        this.setImage(image);
        this.setFitWidth(20);
        this.setFitHeight(20);
        setFocusTraversable(true);
        getStyleClass().add("Checkbox");


        InputStream hoverStream = this.getClass().getResourceAsStream("/client/images/CheckmarkHover.png");
        assert hoverStream != null;
        Image hoverImage = new Image(hoverStream);

        this.setOnMouseEntered(event -> {
            if (!checked.getValue())
                this.setImage(hoverImage);           //on hover, set image to hoverImage (black borders)
        });

        this.setOnMouseExited(event -> {
            if (!checked.getValue())
                this.setImage(image);                //set image back to default when not on hover anymore
        });

        InputStream clickedStream = this.getClass().getResourceAsStream("/client/images/CheckmarkOn.png");
        assert clickedStream != null;
        Image clickedImage = new Image(clickedStream);

        this.checkedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                this.setImage(clickedImage);
            }
            else {
                this.setImage(image);
            }
        }));

        this.setOnMouseClicked(event -> {
            requestFocus();
            checked.setValue(!checked.getValue());
        });

    }
}
