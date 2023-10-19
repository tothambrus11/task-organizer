package client.components;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


// A component that automatically sets the height of the TextArea to the height of the text. The width is fixed to the
// preferred width of the TextArea and the text should wrap to the next line

public class AutoSizedTextArea extends TextArea {
    public AutoSizedTextArea() {
        super();
        getStyleClass().add("AutoSizedTextArea");
        setWrapText(true);


        setPrefHeight(getContentHeight());
        textProperty().addListener((observable, oldValue, newValue) -> {
            setPrefHeight(getContentHeight());
        });

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
                event.consume();
                Node node = (Node) event.getSource();
                KeyEvent newEvent
                        = new KeyEvent(event.getSource(),
                        event.getTarget(), event.getEventType(),
                        event.getCharacter(), event.getText(),
                        event.getCode(), event.isShiftDown(),
                        true, event.isAltDown(),
                        event.isMetaDown());

                node.fireEvent(newEvent);
            }
        });
    }

    private double getContentHeight() {
        var el = lookup(".text");
        if (el == null) return 1;
        return el.getLayoutBounds().getHeight() + getPadding().getTop() + getPadding().getBottom();
    }
}
