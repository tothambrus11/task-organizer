package client.components;

import client.SimpleFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


import java.io.InputStream;

public class ConnectionErrorPopup extends AnchorPane {

    @FXML
    ImageView warningImage;

    public ConnectionErrorPopup() {
        super();
        SimpleFXMLLoader._initView("/client/components/ConnectionErrorPopup.fxml", this);
        this.setVisible(false);
        InputStream stream = this.getClass().getResourceAsStream("/client/images/warningSign.png");

        assert stream != null;

        Image image = new Image(stream);

        warningImage.setImage(image);

        //this.setVisible(true);
        this.setViewOrder(-1000);
    }


}
