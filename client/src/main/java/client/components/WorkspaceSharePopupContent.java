package client.components;

import client.models.BoardPreviewModel;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class WorkspaceSharePopupContent extends HBox {
    private final TextField textField;

    public WorkspaceSharePopupContent() {
        getStyleClass().add("ShareWorkspacePopupContent");

        var label = new Text("Join key:");
        label.getStyleClass().add("ShareWorkspacePopupContentLabel");

        HBox textFieldBox = new HBox();

        textField = new TextField();
        textField.setPrefWidth(111);
        textField.setPrefHeight(43);
        textField.getStyleClass().add("TextFieldSharePopup");

        ImageView imageView = new ImageView(new Image("/client/images/content_copy.png"));
        textFieldBox.getChildren().addAll(textField, imageView);
        textFieldBox.setStyle("-fx-border-width: 1px; -fx-border-color: DCDCDC; -fx-border-radius: 5px;");
        textField.setEditable(false);

        HBox hBox2 = new HBox(label, textFieldBox);
        hBox2.setSpacing(30);
        hBox2.setAlignment(Pos.CENTER);

        imageView.setOnMouseEntered(event -> setCursor(Cursor.HAND));
        imageView.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(textField.getText());
            clipboard.setContent(content);
        });

        getChildren().add(hBox2);
    }


    public void setModel(BoardPreviewModel boardPreviewModel) {
        textField.setText(boardPreviewModel.getJoinKey());
    }

    public interface Factory {
        WorkspaceSharePopupContent create();
    }
}
