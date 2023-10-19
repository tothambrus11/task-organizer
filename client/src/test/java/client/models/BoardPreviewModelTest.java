package client.models;

import client.utils.FXTest;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.models.BoardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BoardPreviewModelTest extends FXTest {
    private Injector injector;
    private FrontendModelTestModule module;

    @BeforeEach
    public void setUp() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    public void testConstructors() {
        var workspaceModel = mock(WorkspaceModel.class);
        var boardPreviewModel0 = new BoardPreviewModel(workspaceModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardPreviewModel0.getId());
        var boardInfo = mock(BoardInfo.class);
        var boardPreviewModel1 = new BoardPreviewModel(workspaceModel, boardInfo);
        assertEquals(boardInfo.getTitle(), boardPreviewModel1.getTitle());
        var boardPreviewModel2 = new BoardPreviewModel(workspaceModel);
        assertEquals(workspaceModel, boardPreviewModel2.getParentWorkspace());
    }

    @Test
    public void testGettersAndSetters() {
        var workspaceModel = mock(WorkspaceModel.class);
        var boardPreviewModel = new BoardPreviewModel(workspaceModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        boardPreviewModel.setJoinKey("Test join key");
        assertEquals("Test join key", boardPreviewModel.getJoinKey());
        assertEquals("Test join key", boardPreviewModel.joinKeyProperty().get());
        var lastAccessTime = mock(LocalDateTime.class);
        boardPreviewModel.setLastAccessTime(lastAccessTime);
        assertEquals(lastAccessTime, boardPreviewModel.getLastAccessTime());
        boardPreviewModel.setTitle("Test title");
        assertEquals("Test title", boardPreviewModel.getTitle());
        assertEquals("Test title", boardPreviewModel.titleProperty().get());
        boardPreviewModel.setCreator("Test creator");
        assertEquals("Test creator", boardPreviewModel.getCreator());
        assertEquals("Test creator", boardPreviewModel.creatorProperty().get());
        boardPreviewModel.setViewOrder(1);
        assertEquals(1, boardPreviewModel.getViewOrder());
    }

    @Test
    public void testLeaveShouldCallLeaveBoardPreview() {
        var workspaceModel = mock(WorkspaceModel.class);
        var boardPreviewModel = new BoardPreviewModel(workspaceModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        boardPreviewModel.leave();
        verify(workspaceModel).leaveBoardPreview(boardPreviewModel);
    }

    @Test
    public void testDeleteShouldCallDeleteBoardPreview() {
        var workspaceModel = mock(WorkspaceModel.class);
        var boardPreviewModel = new BoardPreviewModel(workspaceModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        boardPreviewModel.delete();
        verify(workspaceModel).deleteBoardPreview(boardPreviewModel);
    }
}
