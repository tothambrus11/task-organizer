package client.components;

import client.App;
import client.SimpleFXMLLoader;
import client.contexts.UserContext;
import client.models.BoardPreviewModel;
import client.utils.AppPalette;
import client.utils.StyleUtils;
import client.views.BoardPreviewCell;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class BoardPreview extends AnchorPane {

    private final App app;
    private final UserContext userContext;

    private final SimplePopup shareBoardPopup;
    BoardPreviewModel boardPreviewModel;
    BoardPreviewCell boardPreviewCell;
    @FXML
    HBox boardhbox;
    @FXML
    Text boardPrevTitle;
    @FXML
    Text boardPrevCreator;
    @FXML
    IconButton leaveBoardButton;
    @FXML
    IconButton deleteBoardButton;
    @FXML
    IconButton shareBoardButton;
    @FXML
    WorkspaceSharePopupContent workspaceSharePopupContent;

    @Inject
    public BoardPreview(App app,
                        AppPalette appPalette,
                        StyleUtils styleUtils,
                        WorkspaceSharePopupContent.Factory workspaceSharePopupContentFactory,
                        UserContext userContext,
                        SimplePopup.Factory simplePopupFactory,
                        SimpleFXMLLoader fxmlLoader) {
        this.app = app;
        this.userContext = userContext;

        fxmlLoader.initView("/client/components/BoardPreview.fxml", this);
        getStyleClass().add("BoardPreview");
        setFocusTraversable(false);

        var boardPreviewBackground = styleUtils.backgroundOf(this);
        for (Node child : boardhbox.getChildren()) {
            ((IconButton) child).backgroundColorProperty().bind(boardPreviewBackground);
            ((IconButton) child).setForegroundColor(appPalette.getDefaultIconForeground());
        }

        this.setOnMouseClicked(event -> {
            app.showBoard(boardPreviewModel.getJoinKey());
            System.out.println("BoardPreview clicked");
        });

        // init popup
        workspaceSharePopupContent = workspaceSharePopupContentFactory.create();
        shareBoardPopup = simplePopupFactory.create();
        shareBoardPopup.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/client/app.css")).toExternalForm());
        shareBoardPopup.setFollowed(shareBoardButton);
        shareBoardPopup.setInnerContent(workspaceSharePopupContent);

        deleteBoardButton.setOnAction(event -> {
            boardPreviewModel.delete();
        });
        leaveBoardButton.setOnAction(event -> {
            boardPreviewModel.leave();
        });
        shareBoardButton.setOnAction(event -> {
            shareBoardPopup.showPopup();
        });

        leaveBoardButton.setTooltip(new Tooltip("Leave Board"));
        deleteBoardButton.setTooltip(new Tooltip("Delete Board"));
        shareBoardButton.setTooltip(new Tooltip("Share Board"));
    }

    private void updateUI() {
        if (userContext.getIsAdmin()) {
            leaveBoardButton.setVisibility(false);
        } else {
            leaveBoardButton.setVisibility(true);
        }
    }

    public void setModel(BoardPreviewModel boardPreviewModel) {
        this.boardPreviewModel = boardPreviewModel;
        boardPrevTitle.textProperty().bind(boardPreviewModel.titleProperty());
        boardPrevCreator.textProperty().bind(boardPreviewModel.creatorProperty());
        workspaceSharePopupContent.setModel(boardPreviewModel);

        updateUI();
    }

    public interface Factory {
        BoardPreview create();
    }
}
