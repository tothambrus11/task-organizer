package client.models;

import client.utils.FXTest;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.models.Tag;
import commons.utils.SmartColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TagModelTest extends FXTest {
    @Inject
    TagModel.Factory tagFactory;
    private Injector injector;
    private FrontendModelTestModule module;

    @BeforeEach
    public void setUp() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    public void testGettersSetters() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "-", new SmartColor(1.0, 1.0, 1.0, 0.5));

        tagModel.setName("Test name");
        tagModel.setColor(new SmartColor(1.0, 1.0, 1.0, 0.5));

        assertEquals("Test name", tagModel.getName());
        assertEquals("Test name", tagModel.nameProperty().get());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.getColor());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.colorProperty().get());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), tagModel.getId());
    }

    @Test
    public void emptyNameInConstructorThrowsException() {
        var boardModel = mock(BoardModel.class);
        assertThrows(Exception.class, () -> tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "", new SmartColor(1.0, 1.0, 1.0, 0.5)));
        assertThrows(Exception.class, () -> tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, null, new SmartColor(1.0, 1.0, 1.0, 0.5)));
    }

    @Test
    public void nullColorInConstructorThrowsException() {
        var boardModel = mock(BoardModel.class);
        assertThrows(Exception.class, () -> tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "-", null));
    }

    @Test
    public void fromServerTagConstructorWorksNormally() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(new commons.models.Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1.0, 1.0, 1.0, 0.5)), boardModel);
        assertEquals("Test name", tagModel.getName());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.getColor());
        assertEquals("Test name", tagModel.nameProperty().get());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.getColor());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.colorProperty().get());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), tagModel.getId());
    }

    @Test
    public void testConstructors() {
        var boardModel = mock(BoardModel.class);
        var smartColor = mock(SmartColor.class);
        var tagModel2 = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "Test name", smartColor);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), tagModel2.getId());
        assertEquals(boardModel, tagModel2.getParentBoard());
        assertEquals("Test name", tagModel2.getName());
        assertEquals(smartColor, tagModel2.getColor());
        var tag = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", smartColor);
        var tagModel3 = tagFactory.create(tag, boardModel);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), tagModel3.getId());
        assertEquals(boardModel, tagModel3.getParentBoard());
        assertEquals("Test name", tagModel3.getName());
        assertEquals(smartColor, tagModel3.getColor());
    }

    @Test
    public void testSaveShouldCallUpdateTag() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "w", new SmartColor(1, 1, 1, 1));
        tagModel.save();
        verify(this.module.sessionMock).updateTag(any(Tag.class));
    }

    @Test
    public void testRemoveShouldCallParentBoardDeleteTag() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "e", new SmartColor(1, 1, 1, 1));
        tagModel.remove();
        verify(boardModel).deleteTag(tagModel);
    }

    @Test
    public void testPeerUpdatedShouldUpdateNameAndColor() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"), boardModel, "2", new SmartColor(1, 1, 1, 1));
        var tag = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1.0, 1.0, 1.0, 0.5));
        tagModel.peerUpdated(tag);
        assertEquals("Test name", tagModel.getName());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.getColor());
    }


    @Test
    public void fromServerTagConstructorThrowsExceptionOnEmptyName() {
        var boardModel = mock(BoardModel.class);
        assertThrows(Exception.class, () -> tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "", new SmartColor(1.0, 1.0, 1.0, 0.5)), boardModel));
        assertThrows(Exception.class, () -> tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), null, new SmartColor(1.0, 1.0, 1.0, 0.5)), boardModel));
    }

    @Test
    public void fromServerTagConstructorThrowsExceptionOnNullColor() {
        var boardModel = mock(BoardModel.class);
        assertThrows(Exception.class, () -> tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", null), boardModel));
    }

    @Test
    public void getParentBoard() {
        var boardModel = mock(BoardModel.class);
        assertSame(boardModel, tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1, 1, 1, 1)), boardModel).getParentBoard());
    }

    @Test
    public void removeCallsRemoveTagOnParentBoard() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1, 1, 1, 1)), boardModel);
        tagModel.remove();
        verify(boardModel).deleteTag(tagModel);
    }

    @Test
    public void peerUpdated() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1, 1, 1, 1)), boardModel);
        tagModel.peerUpdated(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name 2", new SmartColor(1.0, 1.0, 1.0, 0.5)));
        assertEquals("Test name 2", tagModel.getName());
        assertEquals("Test name 2", tagModel.nameProperty().get());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.getColor());
        assertEquals(new SmartColor(1.0, 1.0, 1.0, 0.5), tagModel.colorProperty().get());
    }

    @Test
    public void saveCallsSessionContext() {
        var boardModel = mock(BoardModel.class);
        var tagModel = tagFactory.create(new Tag(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test name", new SmartColor(1, 1, 1, 1)), boardModel);
        tagModel.save();
        verify(module.sessionMock).updateTag(any());
    }
}
