package client.components;

import client.SimpleFXMLLoader;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class JoinInputComponent extends AnchorPane {

    @FXML
    KeyJoinText joinKey;
    @FXML
    Button joinButton;

    private Consumer<String> handleJoin;

    public JoinInputComponent() {
        SimpleFXMLLoader._initView("/client/components/JoinInputComponent.fxml", this);
        getStyleClass().add("JoinInputComponent");

        joinKey.focusedProperty().addListener(event -> {
            if(joinKey.isFocused()){
                joinButton.getStyleClass().add("JoinInputIsFocused");
                joinButton.getStyleClass().remove("JoinInputNotFocused");
            } else {
                joinButton.getStyleClass().remove("JoinInputIsFocused");
                joinButton.getStyleClass().add("JoinInputNotFocused");
            }
        });

        joinButton.setFocusTraversable(false);
        joinButton.setOnAction((event -> { handleJoin.accept(joinKey.getText()); }));
    }

    public void setHandleJoin(Consumer<String> handleJoin) {
        this.handleJoin = handleJoin;
    }
}
