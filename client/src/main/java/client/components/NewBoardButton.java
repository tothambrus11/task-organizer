package client.components;

import client.SimpleFXMLLoader;
import javafx.scene.control.Button;

public class NewBoardButton extends Button {
    public NewBoardButton() {
        SimpleFXMLLoader._initView("/client/components/NewBoardButton.fxml", this);
        getStyleClass().add("NewBoardButton");
    }
}
