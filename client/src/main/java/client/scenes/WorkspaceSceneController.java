package client.scenes;

import client.components.*;
import commons.models.BoardInfo;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import client.App;
import client.contexts.UserContext;
import client.models.BoardPreviewModel;
import client.models.WorkspaceModel;
import client.views.WorkspaceView;
import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkspaceSceneController implements Initializable {

    @Inject
    WorkspaceView.Factory workspaceViewFactory;

    @Inject
    private UserContext userContext;

    @Inject
    private App app;

    private WorkspaceView workspaceView;
    private WorkspaceModel workspaceModel;

    @FXML
    HelpPopup helpPopup;

    @FXML
    IconButton logoutButton;
    @FXML
    IconButton resetButton;
    @FXML
    IconButton helpButton;

    @FXML
    JoinInputComponent joinInput;
    @FXML
    NewBoardButton newBoardButton;

    @FXML
    private AnchorPane container;
    @FXML
    AnchorPane listContainer;


    private ScheduledExecutorService refreshExecutor;
    private boolean shouldRefresh = true;

    public void loadBoards() {
        if (userContext.getIsAdmin()) {
            joinInput.setVisible(false);
            newBoardButton.setVisible(false);
            resetButton.setDisable(false);
        } else {
            joinInput.setVisible(true);
            newBoardButton.setVisible(true);
            resetButton.setDisable(true);
        }

        if (userContext.getIsAdmin()) {
            var boards = userContext.retrieveAllBoards();
            populateBoards(boards);
        } else {
            var boards = userContext.getKeychain().getEntriesByRecency();
            populateBoards(boards);
        }
    }

    private void refreshLoop() {
        if (!userContext.getIsLoggedIn())
            shouldRefresh = false;

        if (!shouldRefresh)
            return;

        if (userContext.getIsAdmin()) {
            refreshAdmin();
        } else {
            refreshUser();
        }
    }

    public void populateBoards(List<BoardInfo> boards) {
        workspaceModel.clear();
        for (var board : boards) {
            var preview = new BoardPreviewModel(workspaceModel, board);
            workspaceModel.addBoardPreview(preview);
        }
    }

    private void refreshUser() {
        userContext.getKeychain().refresh(() -> {
            Platform.runLater(() -> {
                if (userContext.getIsLoggedIn() && !userContext.getIsAdmin()) {
                    populateBoards(userContext.getKeychain().getEntriesByRecency());
                }

                refreshLoop();
            });
        });
    }

    private void refreshAdmin() {
        userContext.requestRefresh(boards -> {
            Platform.runLater(() -> {
                if (userContext.getIsLoggedIn() && userContext.getIsAdmin()) {
                    populateBoards(boards);
                    System.out.println("Refreshed admin");
                } else {
                    System.out.println("Admin refresh skipped");
                }

                refreshLoop();
            });
        });
    }

    private void handleLogin() {
        System.out.println("Login event received");

        shouldRefresh = true;
        refreshLoop();
    }

    private void handleLogout() {
        System.out.println("Logout event received");
        shouldRefresh = false;
    }

    private void handleReset() {
        userContext.reset();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workspaceModel = new WorkspaceModel(userContext);

        helpButton.setOnMouseClicked(this::showHelpPopup);
        resetButton.setOnMouseClicked(event -> {
            handleReset();
        });

        logoutButton.setOnAction(event -> {
            app.showUserLogin();
        });

        joinInput.setHandleJoin(app::showBoard);
        newBoardButton.setOnAction(e -> app.createBoard());

        var workspaceListView = workspaceViewFactory.create(workspaceModel);
        AnchorPane.setLeftAnchor(workspaceListView, 0.0);
        AnchorPane.setRightAnchor(workspaceListView, 0.0);
        AnchorPane.setBottomAnchor(workspaceListView, 0.0);
        AnchorPane.setTopAnchor(workspaceListView, 0.0);

        // bring to back
        workspaceListView.setViewOrder(1000);

        listContainer.getChildren().add(workspaceListView);

        userContext.addLoginListener(this::handleLogin);
    }

    @FXML
    public void showHelpPopup(MouseEvent mouseEvent) {
        System.out.println("Help button clicked");
        helpPopup.open();
    }

}
