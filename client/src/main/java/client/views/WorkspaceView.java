package client.views;

import client.models.BoardPreviewModel;
import client.models.WorkspaceModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.scene.control.ListView;

public class WorkspaceView extends ListView<BoardPreviewModel> {

    @Inject
    public WorkspaceView(@Assisted WorkspaceModel workspaceModel, BoardPreviewCell.Factory boardPreviewFactory) {
        super(workspaceModel.getSortedBoardPreviews());
        getStyleClass().add("WorkspaceView");

        setCellFactory(param -> boardPreviewFactory.create());
    }

    public interface Factory {
        WorkspaceView create(WorkspaceModel workspaceModel);
    }

}
