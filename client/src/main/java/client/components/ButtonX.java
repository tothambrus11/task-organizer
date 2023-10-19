package client.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;

public class ButtonX extends VBox {

    public ButtonX(){
        super();
        InputStream stream = this.getClass().getResourceAsStream("/client/images/buttonX.png");
        InputStream hoverStream = this.getClass().getResourceAsStream("/client/images/hoverX.png");

        assert stream != null;
        Image image = new Image(stream);
        assert hoverStream != null;
        Image hoverImage = new Image(hoverStream);

        ImageView iw = new ImageView();
        iw.setFitWidth(24);
        iw.setFitHeight(24);
        iw.setImage(image);
        getChildren().add(iw);

        this.setFocusTraversable(true);

        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            iw.setImage(newValue ? hoverImage : image);
        });

    }
}
