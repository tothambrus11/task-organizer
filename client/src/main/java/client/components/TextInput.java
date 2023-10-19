package client.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class TextInput extends TextField {
    public TextInput(){
        super();
        this.getStyleClass().add("TextInput");
        this.setPadding(new Insets(12,12,12,12));
        this.setPrefWidth(400);
    }
}
