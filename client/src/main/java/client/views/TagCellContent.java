package client.views;

import client.SimpleFXMLLoader;
import client.components.IconButton;
import client.components.RoundColorPicker;
import client.models.TagModel;
import client.utils.AppPalette;
import client.utils.StyleUtils;
import com.google.inject.Inject;
import commons.utils.SmartColor;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static javafx.scene.input.KeyCode.ESCAPE;

public class TagCellContent extends VBox {
    private final BooleanProperty isEditing = new SimpleBooleanProperty(false);
    private ObjectProperty<SmartColor> pickerColor = new SimpleObjectProperty<>(new SmartColor(1.0, 1.0, 1.0, 1.0));
    @FXML
    private HBox hBoxForText;
    @FXML
    private TextField textFieldInput;
    @FXML
    private RoundColorPicker roundColorPicker;
    private TagModel tagModel;
    ChangeListener<String> onModelNameChanged = (observable, oldValue, newValue) -> {
        textFieldInput.setText(tagModel.getName());
    };
    @FXML
    private IconButton editButton;
    private ChangeListener<? super SmartColor> onModelColorChanged = (observable, oldValue, newValue) -> {
        roundColorPicker.setColor(newValue);
    };

    @Inject
    public TagCellContent(StyleUtils styleUtils, AppPalette appPalette, SimpleFXMLLoader fxmlLoader) {
        fxmlLoader.initView("/client/components/TagRow.fxml", this);
        getStyleClass().add("TagCellContent");
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
                if (textFieldInput.getText().isEmpty()) textFieldInput.setText(tagModel.getName());
                else {
                    tagModel.setName(textFieldInput.getText());
                    tagModel.save();
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

        roundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (tagModel != null && !tagModel.getColor().equals(newValue)) {
                tagModel.setColor(newValue);

                System.out.println("save tag" + tagModel.getColor());
                tagModel.save();
            }
        });
        textFieldInput.mouseTransparentProperty().bind(isEditing.not());

        return textFieldInput;
    }

    public void finishTextEdit() {
        isEditing.set(false);
        textFieldInput.deselect();

        if (textFieldInput.getText().equals(tagModel.getName())) return;
        tagModel.setName(textFieldInput.getText());
        tagModel.save();
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

    public void cancelTextEdit() {
        textFieldInput.setText(tagModel.getName());
        isEditing.set(false);
        textFieldInput.deselect();
    }

    public void setModel(TagModel tagModel) {
        System.out.println("set tag and clear bindings");
        clearBindings();
        this.tagModel = tagModel;
        this.roundColorPicker.setColor(tagModel.getColor());
        this.textFieldInput.setText(tagModel.getName());
        this.tagModel.nameProperty().addListener(onModelNameChanged);
        this.tagModel.colorProperty().addListener(onModelColorChanged);

        System.out.println("set tag and clear bindings done");
    }

    public void clearBindings() {
        if (tagModel != null) {
            tagModel.nameProperty().removeListener(onModelNameChanged);
            tagModel.colorProperty().removeListener(onModelColorChanged);
        }
    }

    public interface Factory {
        TagCellContent create();
    }
}
