package client.components;

import javafx.geometry.Insets;
import javafx.scene.control.PasswordField;

import java.io.InputStream;

public class KeyJoinText extends PasswordField {

    public KeyJoinText() {
        super();
        this.getStyleClass().add("KeyJoinText");
        this.getStyleClass().add("TextInput");
        InputStream inputStream = getClass().getResourceAsStream("/client/images/lock.png");
        assert inputStream != null;
        this.setPrefWidth(160);

        this.setPadding(new Insets(12, 12, 12, 30));
        focusedProperty().addListener(event -> {
            if (isFocused()) {
                this.setStyle("-fx-background-image: url(\"/client/images/lockFocus.png\");");
            } else {
                this.setStyle("-fx-background-image: url(\"/client/images/lock.png\");");
            }
        });
    }
}
