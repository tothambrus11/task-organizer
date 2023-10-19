package client.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class ArrowButton extends ImageView {
    public ArrowButton(){
        super();
        InputStream stream = this.getClass().getResourceAsStream("/client/images/arrow.png");
        InputStream hoverStream = this.getClass().getResourceAsStream("/client/images/arrowHover.png");

        assert stream != null;
        Image image = new Image(stream);
        assert hoverStream != null;
        Image hoverImage = new Image(hoverStream);

        this.setImage(image);

        this.setOnMouseEntered(event -> this.setImage(hoverImage));

        this.setOnMouseExited(event -> this.setImage(image));
    }
}
