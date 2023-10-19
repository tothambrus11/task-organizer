package client.models;

import client.utils.FXTest;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.models.TaskHighlight;
import commons.utils.SmartColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HighlightModelTest extends FXTest {
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
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        assertEquals(uuid, highlightModel.getId());
        assertEquals("Test t", highlightModel.getName());
        assertEquals(smartColor, highlightModel.getBackgroundColor());
        assertEquals(smartColor, highlightModel.getForegroundColor());
    }

    @Test
    public void testGettersAndGetters() {
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        assertEquals(boardModel, highlightModel.getBoard());
        highlightModel.setName("Test t2");
        assertEquals("Test t2", highlightModel.getName());
        assertEquals("Test t2", highlightModel.nameProperty().get());
        highlightModel.setPosition(1L);
        assertEquals(1L, highlightModel.getPosition());
        highlightModel.setBackgroundColor(smartColor);
        assertEquals(smartColor, highlightModel.getBackgroundColor());
        assertEquals(smartColor, highlightModel.backgroundColorProperty().get());
        highlightModel.setForegroundColor(smartColor);
        assertEquals(smartColor, highlightModel.getForegroundColor());
        assertEquals(smartColor, highlightModel.foregroundColorProperty().get());
    }

    @Test
    public void testSaveCallsSaveHighlightOnSessionContext() {
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        highlightModel.save();
        verify(module.sessionMock).saveHighlight(any(TaskHighlight.class));
    }

    @Test
    public void testDeleteCallsRemoveHighlightOnSessionContext() {
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        highlightModel.delete();
        verify(module.sessionMock).removeHighlight(any(UUID.class));
    }

    @Test
    public void toServerHighlightReturnsCorrectTaskHighlight() {
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        var taskHighlight = highlightModel.toServerHighlight();
        assertEquals(uuid, taskHighlight.getId());
        assertEquals("Test t", taskHighlight.getName());
        assertEquals(smartColor, taskHighlight.getBackgroundColor());
        assertEquals(smartColor, taskHighlight.getForegroundColor());
        assertEquals(0L, taskHighlight.getPosition());
    }

    @Test
    public void testPeerUpdatedUpdatesCorrectly() {
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var smartColor = new SmartColor(0, 0, 0, 1);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test t", smartColor, smartColor, boardModel, 0L, module.sessionMock);
        var taskHighlight = new TaskHighlight(uuid, "Test t2", smartColor, smartColor, 1L);
        highlightModel.peerUpdated(taskHighlight);
        assertEquals("Test t2", highlightModel.getName());
        assertEquals(smartColor, highlightModel.getBackgroundColor());
        assertEquals(smartColor, highlightModel.getForegroundColor());
        assertEquals(1L, highlightModel.getPosition());
    }

    @Test
    public void testCompareToShouldReturnCorrectValue() {
        var taskHighlight = mock(TaskHighlight.class);
        var boardModel = mock(BoardModel.class);
        var highlightModel = new HighlightModel(taskHighlight, boardModel, module.sessionMock);
        highlightModel.setPosition(0L);
        var highlightModel2 = new HighlightModel(taskHighlight, boardModel, module.sessionMock);
        highlightModel2.setPosition(1L);
        assertEquals(-1, HighlightModel.compareTo(highlightModel, highlightModel2));
        assertEquals(1, HighlightModel.compareTo(highlightModel2, highlightModel));
        assertEquals(0, HighlightModel.compareTo(highlightModel, highlightModel));

    }
}
