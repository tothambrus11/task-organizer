package client.scenes;

import client.components.*;
import client.contexts.SessionContext;
import client.models.BoardModel;
import client.models.TaskModel;
import client.views.BoardView;
import client.views.TagsView;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Logger;


public class BoardSceneController {
    private static final Logger logger = Logger.getLogger("BoardSceneController");
    @FXML
    private TagsView tagsView;


    @FXML
    private AnchorPane container;

    @Inject
    private BoardView.Factory boardViewFactory;

    @Inject
    private BoardModel.Factory boardModelFactory;

    @Inject
    private SessionContext sessionContext;

    private BoardView boardView;
    private Header header;
    @FXML
    private TaskDetailsPopup taskDetailsPopup;

    @FXML
    private ConnectionErrorPopup connectionErrorPopup;
    @Inject
    private TagsView.Factory tagsViewFactory;

    @Inject
    private TaskDetailsPopup.Factory taskDetailsPopupFactory;

    @Inject
    private HelpPopup helpPopup;
    @Inject
    private IconButton helpButton;
    @Inject
    private Header.Factory headerFactory;

    public void loadBoard(String boardKey) {
        sessionContext.setOnModelInitialized((boardModel) -> {
            if (!container.getChildren().isEmpty()) {
                container.getChildren().clear();
            }
            // creating board view
            boardView = boardViewFactory.create(boardModel);
            AnchorPane.setTopAnchor(boardView, 62.0);
            AnchorPane.setBottomAnchor(boardView, 0.0);
            AnchorPane.setLeftAnchor(boardView, 0.0);
            AnchorPane.setRightAnchor(boardView, 0.0);
            this.container.getChildren().add(boardView);


            // creating header
            header = headerFactory.create(boardModel);
            AnchorPane.setTopAnchor(header, 0.0);
            AnchorPane.setLeftAnchor(header, 0.0);
            AnchorPane.setRightAnchor(header, 0.0);
            this.container.getChildren().add(header);

            // creating task details view
            taskDetailsPopup = taskDetailsPopupFactory.create(boardModel);
            AnchorPane.setTopAnchor(taskDetailsPopup, 0.0);
            AnchorPane.setBottomAnchor(taskDetailsPopup, 0.0);
            AnchorPane.setLeftAnchor(taskDetailsPopup, 0.0);
            AnchorPane.setRightAnchor(taskDetailsPopup, 0.0);
            this.container.getChildren().add(taskDetailsPopup);

            // Creating error popup
            connectionErrorPopup = new ConnectionErrorPopup();
            AnchorPane.setTopAnchor(connectionErrorPopup, 0.0);
            AnchorPane.setBottomAnchor(connectionErrorPopup, 0.0);
            AnchorPane.setLeftAnchor(connectionErrorPopup, 0.0);
            AnchorPane.setRightAnchor(connectionErrorPopup, 0.0);
            this.container.getChildren().add(connectionErrorPopup);

            // creating help popup
//            <HBox styleClass="bottom-action-bar"
//            AnchorPane.bottomAnchor="32"
//            AnchorPane.rightAnchor="32" viewOrder="-1111">
//        <IconButton fx:id="helpButton" size="LARGE" foregroundColor="white" backgroundColor="#2F373E"
//            source="client/icons/help_button.png"/>
//    </HBox>
//    <HelpPopup viewOrder="-11111" fx:id="helpPopup" AnchorPane.leftAnchor="0" AnchorPane.rightAnchor="0"
//            AnchorPane.bottomAnchor="0" AnchorPane.topAnchor="0"/>
//
//                actually:


        });

        sessionContext.join(boardKey);
    }

    public void viewTaskDetails(TaskModel task) {
        taskDetailsPopup.open(task);
    }

    public void closeTaskDetails() {
        taskDetailsPopup.close();
    }

}
