package client.views;

import client.SimpleFXMLLoader;
import client.components.IconButton;
import client.components.RoundColorPicker;
import client.models.BoardModel;
import client.utils.AppPalette;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import commons.Constants;
import commons.utils.SmartColor;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CustomizationPopupContent extends VBox {
    @FXML
    private RoundColorPicker boardBackgroundColorPicker;
    @FXML
    private RoundColorPicker boardForegroundColorPicker;
    @FXML
    private RoundColorPicker listBackgroundColorPicker;
    @FXML
    private RoundColorPicker listForegroundColorPicker;
    @FXML
    private IconButton resetBoardColorsButton;
    @FXML
    private IconButton resetListColorsButton;
    @FXML
    private HBox listContainer;
    @FXML
    private IconButton addHighlightButton;
    @FXML
    private IconButton removeHighlightButton;
    @FXML
    private IconButton makeDefaultButton;

    @AssistedInject
    public CustomizationPopupContent(@Assisted BoardModel boardModel, AppPalette appPalette, HighlightListView.Factory highlightListViewFactory) {
        this.getStyleClass().add("SharePopup");
        SimpleFXMLLoader._initView("/client/views/CustomizationPopup.fxml", this);

        boardModel.boardBackgroundColorProperty().addListener((observable, oldValue, newValue) -> {
            boardBackgroundColorPicker.setColor(newValue);
        });

        boardModel.boardForegroundColorProperty().addListener((observable, oldValue, newValue) -> {
            boardForegroundColorPicker.setColor(newValue);
        });

        boardModel.listBackgroundColorProperty().addListener((observable, oldValue, newValue) -> {
            listBackgroundColorPicker.setColor(newValue);
        });

        boardModel.listForegroundColorProperty().addListener((observable, oldValue, newValue) -> {
            listForegroundColorPicker.setColor(newValue);
        });

        boardBackgroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (!boardModel.getBoardBackgroundColor().equals(newValue)) {
                boardModel.setBoardBackgroundColor(newValue);
                boardModel.save();
            }
        });

        boardForegroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (!boardModel.getBoardForegroundColor().equals(newValue)) {
                boardModel.setBoardForegroundColor(newValue);
                boardModel.save();
            }
        });


        listBackgroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (!boardModel.getListBackgroundColor().equals(newValue)) {
                boardModel.setListBackgroundColor(newValue);
                boardModel.save();
            }
        });

        listForegroundColorPicker.colorProperty().addListener((observable, oldValue, newValue) -> {
            if (!boardModel.getListForegroundColor().equals(newValue)) {
                boardModel.setListForegroundColor(newValue);
                boardModel.save();
            }
        });

        boardBackgroundColorPicker.setColor(boardModel.getBoardBackgroundColor());
        boardForegroundColorPicker.setColor(boardModel.getBoardForegroundColor());
        listBackgroundColorPicker.setColor(boardModel.getListBackgroundColor());
        listForegroundColorPicker.setColor(boardModel.getListForegroundColor());

        resetBoardColorsButton.setForegroundColor(appPalette.getDefaultIconForeground());
        resetListColorsButton.setForegroundColor(appPalette.getDefaultIconForeground());

        resetBoardColorsButton.visibleProperty().bind(
                boardModel.boardBackgroundColorProperty()
                        .isEqualTo(Constants.DEFAULT_BOARD_BACKGROUND_COLOR).not()
                        .or(boardModel.boardForegroundColorProperty().isEqualTo(Constants.DEFAULT_BOARD_FOREGROUND_COLOR).not())
        );

        resetListColorsButton.visibleProperty().bind(
                boardModel.listBackgroundColorProperty()
                        .isEqualTo(Constants.DEFAULT_LIST_BACKGROUND_COLOR).not()
                        .or(boardModel.listForegroundColorProperty().isEqualTo(Constants.DEFAULT_LIST_FOREGROUND_COLOR).not())
        );

        resetBoardColorsButton.setOnAction(event -> {
            boardModel.setBoardBackgroundColor(Constants.DEFAULT_BOARD_BACKGROUND_COLOR);
            boardModel.setBoardForegroundColor(Constants.DEFAULT_BOARD_FOREGROUND_COLOR);
            boardModel.save();
        });

        resetListColorsButton.setOnAction(event -> {
            boardModel.setListBackgroundColor(Constants.DEFAULT_LIST_BACKGROUND_COLOR);
            boardModel.setListForegroundColor(Constants.DEFAULT_LIST_FOREGROUND_COLOR);
            boardModel.save();
        });

        Tooltip.install(resetBoardColorsButton, new Tooltip("Reset board colors"));
        Tooltip.install(resetListColorsButton, new Tooltip("Reset list colors"));
        Tooltip.install(boardBackgroundColorPicker, new Tooltip("Background"));
        Tooltip.install(boardForegroundColorPicker, new Tooltip("Foreground"));
        Tooltip.install(listBackgroundColorPicker, new Tooltip("Background"));
        Tooltip.install(listForegroundColorPicker, new Tooltip("Foreground"));


        // init listview
        HighlightListView highlightListView = highlightListViewFactory.create(boardModel);
        HBox.setHgrow(highlightListView, Priority.ALWAYS);
        listContainer.getChildren().add(highlightListView);


        // init action buttons
        addHighlightButton.setForegroundColor(appPalette.getDefaultIconForeground());
        removeHighlightButton.setForegroundColor(appPalette.getDefaultIconForeground());
        makeDefaultButton.setForegroundColor(appPalette.getDefaultIconForeground());

        addHighlightButton.setOnAction(event -> {
            boardModel.createHighlight("New Highlight", SmartColor.valueOf("black"), SmartColor.valueOf("#aaffaa"));
            boardModel.save();
        });

        removeHighlightButton.setOnAction(event -> {
            boardModel.removeHighlight(highlightListView.getSelectionModel().getSelectedItem());
            boardModel.save();
        });


    }

    public interface Factory {
        CustomizationPopupContent create(BoardModel boardModel);
    }
}
