package client.models;

import client.utils.FXTest;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.models.Tag;
import commons.models.Task;
import commons.utils.SmartColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class TaskModelTest extends FXTest {
    @Inject
    TaskModel.Factory taskFactory;
    @Inject
    TaskListModel.Factory taskListFactory;
    @Inject
    TagModel.Factory tagFactory;
    private Injector injector;
    private FrontendModelTestModule module;
    @Inject
    SubTaskModel.Factory subTaskFactory;

    @BeforeEach
    public void setUp() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    public void testConstructorsShouldInitTags() {
        var serverTask = new Task(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test title", "Test description", BigFraction.ONE_HALF);
        var tag = new Tag(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Test tag", new SmartColor(0, 0, 0, 1));
        serverTask.getTags().add(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        var boardModel = mock(BoardModel.class);
        var taskListModel = taskListFactory.create(boardModel);
        var taskModel = taskFactory.create(taskListModel, serverTask);
        assertEquals(1, taskModel.getTags().size());
    }

    @Test
    public void testGettersAndSetters() {
        var taskModel = taskFactory.create(mock(TaskListModel.class), UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Runnable runnable = mock(Runnable.class);
        assertNull(taskModel.getRequestFocusCallback());
        taskModel.setRequestFocusCallback(runnable);
        assertEquals(runnable, taskModel.getRequestFocusCallback());
        taskModel.setPosition(BigFraction.ONE_TENTH);
        assertEquals(BigFraction.ONE_TENTH, taskModel.getPosition());
        assertEquals(BigFraction.ONE_TENTH, taskModel.positionProperty().get());
        taskModel.setTitle("Test title");
        assertEquals("Test title", taskModel.getTitle());
        assertEquals("Test title", taskModel.titleProperty().get());
        taskModel.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), taskModel.getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), taskModel.idProperty().get());
        var taskListModel = mock(TaskListModel.class);
        taskModel.setParentTaskList(taskListModel);
        assertEquals(taskListModel, taskModel.getParentTaskList());
        assertEquals(taskListModel, taskModel.parentTaskListProperty().get());
        taskModel.setDescription("Test description");
        assertEquals("Test description", taskModel.getDescription());
        assertEquals("Test description", taskModel.descriptionProperty().get());
        var highlight = mock(HighlightModel.class);
        taskModel.setHighlight(highlight);
        assertEquals(highlight, taskModel.getHighlight());
        assertEquals(highlight, taskModel.highlightProperty().get());
        var subTaskModel = mock(SubTaskModel.class);
        assertEquals(0, taskModel.getSubtasks().size());
    }

    @Test
    public void testAddTagShouldSessionContextCallAddTagToTask() {
        var taskListModel = mock(TaskListModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000003"), mock(BoardModel.class), "A", new SmartColor(0, 0, 0, 1));
        var taskModel = taskFactory.create(taskListModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskModel.addTag(tagModel);
        assertEquals(1, taskModel.getTags().size());
        verify(this.module.sessionMock).addTagToTask(UUID.fromString("00000000-0000-0000-0000-000000000002"), UUID.fromString("00000000-0000-0000-0000-000000000003"));
    }

    @Test
    public void testRemoveTagShouldSessionContextCallRemoveTagFromTask() {
        var taskListModel = mock(TaskListModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000003"), mock(BoardModel.class), "A", new SmartColor(0, 0, 0, 1));
        var taskModel = taskFactory.create(taskListModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskModel.addTag(tagModel);
        taskModel.removeTag(tagModel);
        assertEquals(0, taskModel.getTags().size());
        verify(this.module.sessionMock).removeTagFromTask(UUID.fromString("00000000-0000-0000-0000-000000000002"), UUID.fromString("00000000-0000-0000-0000-000000000003"));
    }

    @Test
    public void testOnTagDeletedWorksCorrectly() {
        var taskModel = taskFactory.create(mock(TaskListModel.class), UUID.fromString("00000000-0000-0000-0000-000000000002"));
        var boardModel = mock(BoardModel.class);
        var smartColor = new SmartColor(0, 0, 0, 1);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000003"), boardModel, "A", smartColor);
        var tagModel2 = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000004"), boardModel, "B", smartColor);
        var tagModel3 = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000005"), boardModel, "C", smartColor);
        taskModel.addTag(tagModel);
        taskModel.addTag(tagModel2);
        taskModel.addTag(tagModel3);
        assertEquals(3, taskModel.getTags().size());
        taskModel.onTagDeleted(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        assertEquals(2, taskModel.getTags().size());
        taskModel.onTagDeleted(UUID.fromString("00000000-0000-0000-0000-000000000006"));
        assertEquals(2, taskModel.getTags().size());

    }

    @Test
    public void testSaveShouldSessionContextCallUpdateTask() {
        var taskListModel = mock(TaskListModel.class);
        var taskModel = taskFactory.create(taskListModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskModel.save();
        verify(this.module.sessionMock).updateTask(any(Task.class));
    }

    @Test
    public void testRemoveParentTaskListShouldCallRemoveTask() {
        var taskListModel = mock(TaskListModel.class);
        var taskModel = taskFactory.create(taskListModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskModel.remove();
        verify(taskListModel).removeTask(taskModel);
    }

    @Test
    public void testPeerRemovedTagShouldRemoveTagFromTags() {
        var taskListModel = mock(TaskListModel.class);
        var tagModel = tagFactory.create(UUID.fromString("00000000-0000-0000-0000-000000000003"), mock(BoardModel.class), "A", new SmartColor(0, 0, 0, 1));
        var taskModel = taskFactory.create(taskListModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskModel.addTag(tagModel);
        assertEquals(1, taskModel.getTags().size());
        taskModel.peerRemovedTag(tagModel);
        assertEquals(0, taskModel.getTags().size());
    }

}
