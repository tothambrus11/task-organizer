package client.views;

import client.SimpleFXMLLoader;
import client.components.IconButton;
import client.components.RoundColorPicker;
import client.models.HighlightModel;
import client.utils.AppPalette;
import client.utils.StyleUtils;
import com.google.inject.assistedinject.AssistedInject;
import commons.utils.SmartColor;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static javafx.scene.input.KeyCode.ESCAPE;

public class HighlightCellContent extends VBox {
    private HighlightModel highlightModel;
    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);

    @FXML
    private RoundColorPicker backgroundColorPicker;
    ChangeListener<SmartColor> onBackgroundColorChanged = (o1, o, newColor) -> {
        backgroundColorPicker.setColor(newColor);
    };
    @FXML
    private RoundColorPicker foregroundColorPicker;
    ChangeListener<SmartColor> onForegroundColorChanged = (o1, o, newColor) -> {
        foregroundColorPicker.setColor(newColor);
    };
    @FXML
    private TextField textFieldInput;
    @FXML
    private HBox hBoxForText;

    ChangeListener<String> onTextChanged = (o1, o, newText) -> {
        textFieldInput.setText(newText);
    };
    @FXML
    private IconButton editButton;

    @AssistedInject
    public HighlightCellContent(SimpleFXMLLoader fxmlLoader, AppPalette appPalette, StyleUtils styleUtils) {
        fxmlLoader.initView("/client/views/HighlightCellContent.fxml", this);
        getStyleClass().add("HighlightCellContent");


        this.hoverProperty().addListener((v, oldValue, newValue) -> {
            if (newValue) {
                editButton.setOpacity(1.0);
            } else {
                editButton.setOpacity(0.0);
            }
        });
        editButton.backgroundColorProperty().bind(styleUtils.backgroundOf(this));
        editButton.setForegroundColor(appPalette.getDefaultIconForeground());


        initTextField();
    }


    private TextField initTextField() {
        //textFieldInput.setMaxWidth(1000000);

        textFieldInput.setEditable(false);
        textFieldInput.setMouseTransparent(true);
        textFieldInput.setFocusTraversable(false);

        textFieldInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                textFieldInput.getParent().requestFocus();
                event.consume();
            }
            if (event.getCode() == ESCAPE) {
                cancelTextEdit();
                event.consume();
                textFieldInput.getParent().requestFocus();
            }
        });
        hBoxForText.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                textFieldInput.setEditable(true);
                textFieldInput.requestFocus();
                isEditing.set(true);
            }
        });
        textFieldInput.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                textFieldInput.setEditable(false);
                isEditing.set(false);
                if (textFieldInput.getText().isEmpty()) textFieldInput.setText(highlightModel.getName());
                else {
                    highlightModel.setName(textFieldInput.getText());
                    highlightModel.save();
                }
            }
        }));
        isEditing.addListener((observable, oldValue, newValue) -> {
            if (isEditing.get()) {
                System.out.println("isEditing");
                editButton.setSource("/client/icons/check_small.png");
            } else {
                System.out.println("isNotEditing");
                editButton.setSource("/client/icons/edit_small.png");
            }
        });
        editButton.setOnAction(event -> {
            if (isEditing.get()) {
                finishTextEdit();
            } else {
                actionEditInline(event);
            }
        });

        backgroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (highlightModel != null && !highlightModel.getBackgroundColor().equals(newValue)) {
                highlightModel.setBackgroundColor(newValue);

                System.out.println("save highlight " + highlightModel.getBackgroundColor());
                highlightModel.save();
            }
        });

        foregroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (highlightModel != null && !highlightModel.getForegroundColor().equals(newValue)) {
                highlightModel.setForegroundColor(newValue);

                System.out.println("save highlight " + highlightModel.getForegroundColor());
                highlightModel.save();
            }
        });
        textFieldInput.mouseTransparentProperty().bind(isEditing.not());

        return textFieldInput;
    }

    public void finishTextEdit() {
        isEditing.set(false);
        textFieldInput.deselect();

        if (textFieldInput.getText().equals(highlightModel.getName())) return;
        highlightModel.setName(textFieldInput.getText());
        highlightModel.save();
    }


    public void cancelTextEdit() {
        textFieldInput.setText(highlightModel.getName());
        isEditing.set(false);
        textFieldInput.deselect();
    }
    
    public void actionEditInline(Event e) {
        isEditing.set(true);
        textFieldInput.requestFocus();
        textFieldInput.setEditable(true);

        // Select all text after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Platform.runLater(() -> textFieldInput.selectAll());
        }).start();
    }
    
    public void setModel(HighlightModel highlightModel) {
        clearBindings();
        this.highlightModel = highlightModel;
        backgroundColorPicker.setColor(highlightModel.getBackgroundColor());
        foregroundColorPicker.setColor(highlightModel.getForegroundColor());
        textFieldInput.setText(highlightModel.getName());

        highlightModel.nameProperty().addListener(onTextChanged);
        highlightModel.backgroundColorProperty().addListener(onBackgroundColorChanged);
        highlightModel.foregroundColorProperty().addListener(onForegroundColorChanged);
    }

    private void clearBindings() {
        if (highlightModel == null) return;
        highlightModel.nameProperty().removeListener(onTextChanged);
        highlightModel.backgroundColorProperty().removeListener(onBackgroundColorChanged);
        highlightModel.foregroundColorProperty().removeListener(onForegroundColorChanged);
    }

    public interface Factory {
        HighlightCellContent create();
    }
}
