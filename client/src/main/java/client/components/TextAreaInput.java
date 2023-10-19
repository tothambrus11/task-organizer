package client.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;

public class TextAreaInput extends TextArea {
    public TextAreaInput(){
        super();
        this.getStyleClass().add("TextInput");
        this.setPadding(new Insets(8,8,8,8));
        this.setPrefWidth(400);
        this.setPrefRowCount(1); // Set initial row count to 1
        this.setWrapText(true);

    }
}


