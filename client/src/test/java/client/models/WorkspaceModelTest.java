package client.models;

import client.contexts.UserContext;
import client.utils.FXTest;
import client.utils.Keychain;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class WorkspaceModelTest extends FXTest {
    private Injector injector;
    private FrontendModelTestModule module;

    UserContext userContext = mock(UserContext.class);

    @BeforeEach
    public void setUp() {
        when(userContext.getKeychain()).thenReturn(mock(Keychain.class));

        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    public void testConstructor() {
        var workspaceModel = new WorkspaceModel(userContext);
        assertNotNull(workspaceModel.getSortedBoardPreviews());
        var boardPreviewModel1 = mock(BoardPreviewModel.class);
        var boardPreviewModel2 = mock(BoardPreviewModel.class);
        workspaceModel.addBoardPreview(boardPreviewModel1);
        workspaceModel.addBoardPreview(boardPreviewModel2);
        assertEquals(1, workspaceModel.getSortedBoardPreviews().indexOf(boardPreviewModel1));
        assertEquals(0, workspaceModel.getSortedBoardPreviews().indexOf(boardPreviewModel2));
        var boardPreviewModel3 = mock(BoardPreviewModel.class);
        boardPreviewModel3.setLastAccessTime(mock(LocalDateTime.class));
        workspaceModel.addBoardPreview(boardPreviewModel3);
        assertEquals(0, workspaceModel.getSortedBoardPreviews().indexOf(boardPreviewModel3));
    }

    @Test
    public void testSortedBoardPreviews() {
        var workspaceModel = new WorkspaceModel(userContext);
        var boardPreviewModel1 = mock(BoardPreviewModel.class);
        var boardPreviewModel2 = mock(BoardPreviewModel.class);
        workspaceModel.addBoardPreview(boardPreviewModel1);
        boardPreviewModel2.setLastAccessTime(mock(LocalDateTime.class));
        workspaceModel.addBoardPreview(boardPreviewModel2);
        assertEquals(0, workspaceModel.getSortedBoardPreviews().indexOf(boardPreviewModel2));
    }

    @Test
    public void testAddBoardPreview() {
        var workspaceModel = new WorkspaceModel(userContext);
        var boardPreviewModel = mock(BoardPreviewModel.class);
        workspaceModel.addBoardPreview(boardPreviewModel);
        assertEquals(1, workspaceModel.getSortedBoardPreviews().size());
    }

    @Test
    public void testDeleteBoardPreview() {
        var workspaceModel = new WorkspaceModel(userContext);
        var boardPreviewModel = mock(BoardPreviewModel.class);
        workspaceModel.addBoardPreview(boardPreviewModel);
        assertEquals(1, workspaceModel.getSortedBoardPreviews().size());
        workspaceModel.deleteBoardPreview(boardPreviewModel);
        assertEquals(0, workspaceModel.getSortedBoardPreviews().size());
        workspaceModel.addBoardPreview(boardPreviewModel);
        assertEquals(1, workspaceModel.getSortedBoardPreviews().size());
        workspaceModel.leaveBoardPreview(boardPreviewModel);
        assertEquals(0, workspaceModel.getSortedBoardPreviews().size());
        workspaceModel.addBoardPreview(boardPreviewModel);
        assertEquals(1, workspaceModel.getSortedBoardPreviews().size());
        workspaceModel.clear();
        assertEquals(0, workspaceModel.getSortedBoardPreviews().size());

    }
}
