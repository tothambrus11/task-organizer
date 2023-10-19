package client.components;


import client.models.BoardModel;
import com.google.inject.assistedinject.Assisted;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class SharePopupContent extends VBox {
    private final StringProperty permissionPassword = new SimpleStringProperty("");
    private final BoardModel boardModel;
    private PasswordField passwordField;
    private MySwitch passwordSwitch;
    public SharePopupContent(@Assisted BoardModel boardModel) {
        this.boardModel = boardModel;
        this.getStyleClass().add("SharePopup");

        this.setPrefWidth(268);

        HBox requirePasswordHBox = initRequirePasswordHBox();
        HBox joinKeyHBox = initJoinKeyHBox();
        HBox passwordHBox = initPasswordHBox();


        VBox vbox = new VBox(requirePasswordHBox, joinKeyHBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(16);

        passwordSwitch.state.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                vbox.setSpacing(12);
                vbox.getChildren().remove(1);
                vbox.getChildren().addAll(passwordHBox, joinKeyHBox);
                permissionPassword.bind(passwordField.textProperty());
            } else {
                vbox.setSpacing(16);
                vbox.getChildren().remove(1);
                permissionPassword.unbind();
                permissionPassword.setValue("");
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.requestFocus();
            }
        });

        getChildren().add(vbox);

    }

    public String getPermissionPassword() {
        return permissionPassword.get();
    }

    public StringProperty permissionPasswordProperty() {
        return permissionPassword;
    }

    private HBox initRequirePasswordHBox() {
        passwordSwitch = new MySwitch();
        Label label1 = new Label("Require password for editing: ");
        label1.setWrapText(true);
        HBox.setHgrow(label1, Priority.ALWAYS);


        HBox requirePasswordHBox = new HBox(label1, passwordSwitch);
        HBox.setMargin(passwordSwitch, new Insets(10, 0, 0, 0));
        requirePasswordHBox.setSpacing(51);
        HBox.setHgrow(requirePasswordHBox, Priority.ALWAYS);

        requirePasswordHBox.setAlignment(Pos.CENTER);

        return requirePasswordHBox;
    }

    private HBox initJoinKeyHBox(){
        HBox joinKeyHBox = new HBox(new Label("Join key: "));
        joinKeyHBox.setSpacing(30);
        HBox textFieldBox = new HBox();

        TextField keyField = new TextField();
        keyField.setText(boardModel.getKey());
        keyField.setPrefWidth(111);
        keyField.setPrefHeight(43);
        keyField.getStyleClass().add("TextFieldSharePopup");
        keyField.setEditable(false);

        ImageView imageView = new ImageView(new Image("/client/images/content_copy.png"));
        textFieldBox.getChildren().addAll(keyField, imageView);
        textFieldBox.setStyle("-fx-border-width: 1px; -fx-border-color: DCDCDC; -fx-border-radius: 5px;");


        joinKeyHBox.setAlignment(Pos.CENTER);
        joinKeyHBox.getChildren().add(textFieldBox);

        imageView.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        imageView.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(keyField.getText());
            clipboard.setContent(content);
        });

        return joinKeyHBox;
    }

    private HBox initPasswordHBox() {
        HBox passwordHBox = new HBox();

        passwordField = new KeyJoinText();
        passwordField.setPromptText("********");
        passwordHBox.getChildren().add(passwordField);
        passwordField.setPrefWidth(244);
        passwordField.setFont(Font.font(5));

        return passwordHBox;
    }

    public interface Factory {
        SharePopupContent create(BoardModel boardModel);
    }
}

