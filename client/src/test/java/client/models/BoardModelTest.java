package client.models;

import client.utils.FXTest;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.models.Board;
import commons.models.Tag;
import commons.models.TaskHighlight;
import commons.models.TaskList;
import commons.utils.SmartColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardModelTest extends FXTest {
    private Injector injector;
    private FrontendModelTestModule module;
    @Inject
    BoardModel.Factory boardFactory;
    @Inject
    TaskListModel.Factory taskListFactory;
    @Inject
    TagModel.Factory tagFactory;

    @BeforeEach
    public void setUp() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    public void testRemoveTaskList() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Consumer<TaskListModel> consumer = mock(Consumer.class);
        var taskList = boardModel.createTaskListAtEnd(consumer);
        assertEquals(1, boardModel.getTaskListModelsSorted().indexOf(taskList));
        boardModel.removeTaskList(taskList);
        assertEquals(-1, boardModel.getTaskListModelsSorted().indexOf(taskList));
        var taskList2 = boardModel.createTaskListAtEnd(consumer);
        assertEquals(1, boardModel.getTaskListModelsSorted().indexOf(taskList2));
        taskList2.remove();
        assertEquals(-1, boardModel.getTaskListModelsSorted().indexOf(taskList2));
    }

    @Test
    public void testConstructors() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertNotNull(boardModel);
        var board = new Board();
        board.setTags(new ArrayList<>());
        board.setTaskLists(new ArrayList<>());
        List<TaskHighlight> highlights = new ArrayList<>();
        highlights.add(new TaskHighlight(UUID.fromString("00000000-0000-0000-0000-000000000001"), "name", mock(SmartColor.class), mock(SmartColor.class), 0L));
        board.setDefaultHighlightId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        board.setHighlights(highlights);
        var boardModel2 = boardFactory.create(board);
        assertNotNull(boardModel2);
    }

    @Test
    public void testGettersAndSetters() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        boardModel.setTitle("title");
        assertEquals("title", boardModel.getTitle());
        assertEquals("title", boardModel.titleProperty().get());
        boardModel.setKey("key");
        assertEquals("key", boardModel.getKey());
        assertEquals("key", boardModel.keyProperty().get());
        boardModel.setCreator("creator");
        assertEquals("creator", boardModel.getCreator());
        assertEquals("creator", boardModel.creatorProperty().get());
        boardModel.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), boardModel.getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), boardModel.idProperty().get());
        var smartColor = mock(SmartColor.class);
        boardModel.setListForegroundColor(smartColor);
        assertEquals(smartColor, boardModel.getListForegroundColor());
        assertEquals(smartColor, boardModel.listForegroundColorProperty().get());
        boardModel.setBoardForegroundColor(smartColor);
        assertEquals(smartColor, boardModel.getBoardForegroundColor());
        assertEquals(smartColor, boardModel.boardForegroundColorProperty().get());
        boardModel.setBoardBackgroundColor(smartColor);
        assertEquals(smartColor, boardModel.getBoardBackgroundColor());
        assertEquals(smartColor, boardModel.boardBackgroundColorProperty().get());
        boardModel.setListBackgroundColor(smartColor);
        assertEquals(smartColor, boardModel.getListBackgroundColor());
        assertEquals(smartColor, boardModel.listBackgroundColorProperty().get());
        assertNotNull(boardModel.getHighlightModels());
    }

    @Test
    public void testGetFirstDummyShouldReturnFirstDummy() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals(boardModel.getTaskListModelsSorted().get(0), boardModel.getFirstDummy());
        var consumer = mock(Consumer.class);
        boardModel.createTaskListAtEnd(consumer);
        assertEquals(boardModel.getTaskListModelsSorted().get(0), boardModel.getFirstDummy());
    }

    @Test
    public void testGetLastDummyShouldReturnLastDummy() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals(boardModel.getTaskListModelsSorted().get(1), boardModel.getLastDummy());
        var consumer = mock(Consumer.class);
        boardModel.createTaskListAtEnd(consumer);
        assertEquals(boardModel.getTaskListModelsSorted().get(2), boardModel.getLastDummy());
    }

    @Test
    public void testGetLastTaskListShouldReturnLastTaskList() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals(boardModel.getTaskListModelsSorted().get(0), boardModel.getLastTaskList());
        var consumer = mock(Consumer.class);
        boardModel.createTaskListAtEnd(consumer);
        assertEquals(boardModel.getTaskListModelsSorted().get(1), boardModel.getLastTaskList());
    }

    @Test
    public void testGetListByIdShouldReturnCorrectTaskList() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var consumer = mock(Consumer.class);
        var taskList = boardModel.createTaskListAtEnd(consumer);
        boardModel.getLastTaskList().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(boardModel.getLastTaskList(), boardModel.getListById(UUID.fromString("00000000-0000-0000-0000-000000000001")));
    }

    @Test
    public void testCreateTagSessionContextShouldCallAddTag() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        boardModel.createTag("tag1", smartColor);
        verify(module.sessionMock).addTag(any(Tag.class));
    }

    @Test
    public void testCreateTagShouldThrowExceptionWhenTagNameIsNull() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        assertThrows(IllegalArgumentException.class, () -> boardModel.createTag("", smartColor));
    }

    @Test
    public void testOnDeleteTagShouldRemoveTagFromBoard() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag = boardModel.createTag("tag1", smartColor);
        assertEquals(1, boardModel.getTagModels().size());
        boardModel.onDeleteTag(tag.getId());
        assertEquals(0, boardModel.getTagModels().size());
    }

    @Test
    public void testDeleteTagSessionContextShouldCallremoveTag() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag = boardModel.createTag("tag1", smartColor);
        boardModel.deleteTag(tag);
        verify(module.sessionMock).removeTag(any(UUID.class));
    }

    @Test
    public void testPeerAddedTaskListShouldAddTaskListToTaskListModel() {

        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var taskList = new TaskList(UUID.fromString("00000000-0000-0000-0000-000000000001"), "taskList1");
        taskList.setPosition(BigFraction.ONE_HALF);
        boardModel.peerAddedTaskList(taskList);
        assertEquals(3, boardModel.getTaskListModelsSorted().size());
    }

    @Test
    public void testPeerRemovedTaskListShouldRemoveTaskListFromTaskListModel() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var taskList = new TaskList(UUID.fromString("00000000-0000-0000-0000-000000000001"), "taskList1");
        taskList.setPosition(BigFraction.ONE_HALF);
        boardModel.peerAddedTaskList(taskList);
        assertEquals(3, boardModel.getTaskListModelsSorted().size());
        boardModel.peerRemovedTaskList(taskList.getId());
        assertEquals(2, boardModel.getTaskListModelsSorted().size());
    }

    @Test
    public void testPeerUpdatedTaskListShouldUpdateTaskListInTaskListModel() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var consumer = mock(Consumer.class);
        boardModel.createTaskListAtEnd(consumer);
        var taskListId = boardModel.getLastTaskList().getId();
        var taskList = new TaskList(taskListId, "taskList1");
        boardModel.peerUpdatedTaskList(taskList);
        var taskListModel = boardModel.getListById(taskList.getId());
        assertEquals("taskList1", taskListModel.getTitle());
    }

    @Test
    public void peerUpdatedBoardShouldSetBoardTitle() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var board = mock(Board.class);
        when(board.getTitle()).thenReturn("board1");
        boardModel.peerUpdatedBoard(board);
        assertEquals("board1", boardModel.getTitle());
    }

    @Test
    public void testSaveSessionContextShouldCallUpdateBoard() {
        var board = new Board();
        board.setTags(new ArrayList<>());
        board.setTaskLists(new ArrayList<>());
        List<TaskHighlight> highlights = new ArrayList<>();
        highlights.add(new TaskHighlight(UUID.fromString("00000000-0000-0000-0000-000000000001"), "name", mock(SmartColor.class), mock(SmartColor.class), 0L));
        board.setDefaultHighlightId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        board.setHighlights(highlights);
        var boardModel2 = boardFactory.create(board);
        assertNotNull(boardModel2);
        boardModel2.save();
        verify(module.sessionMock).updateBoard(any(Board.class));
    }

    @Test
    public void testGetTagByIdShouldReturnCorrectTag() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag = boardModel.createTag("tag1", smartColor);
        assertEquals(tag, boardModel.getTagById(tag.getId()));
    }

    @Test
    public void testPeerAddedTagShouldAddTagToTagModel() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000001"), "tag1", smartColor);
        boardModel.peerAddedTag(tag);
        assertEquals(1, boardModel.getTagModels().size());
    }

    @Test
    public void testPeerUpdatedTagShouldUpdateTagInTagModel() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var smartColor = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000001"), "tag1", smartColor);
        boardModel.peerAddedTag(tag);
        assertEquals(1, boardModel.getTagModels().size());
        var smartColor2 = new SmartColor(1.0, 1.0, 1.0, 0.5);
        var tag2 = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000001"), "tag2", smartColor2);
        boardModel.peerUpdatedTag(tag2);
        assertEquals(1, boardModel.getTagModels().size());
        assertEquals("tag2", boardModel.getTagModels().get(0).getName());
    }


    @Test
    public void testPeerAddedTagShouldCallPeerAddedTagOnTaskModel() {
        var boardModel = boardFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var tagModel = mock(TagModel.class);
        var consumer = mock(Consumer.class);
        boardModel.createTaskListAtEnd(consumer);
        var taskModel = boardModel.getLastTaskList().createTaskAtEnd(consumer);
        taskModel.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(0, taskModel.getTags().size());
        boardModel.getTagModels().add(tagModel);
        when(tagModel.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        boardModel.peerAddedTagToTask(taskModel.getId(), tagModel.getId());
        assertEquals(1, taskModel.getTags().size());
    }
}
