package client.components;

import client.SimpleFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;



public class HelpPopup extends PopUp {


    @FXML
    public VBox container;
    @FXML
    public TextButton escapeButton;

    public HelpPopup() {
        super();

        SimpleFXMLLoader._initView("/client/components/HelpPopup.fxml", this);

        escapeButton.setOnMouseClicked(e -> {
            close();
        });

    }
}
