package client.models;

import client.utils.FXTest;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.models.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SubTaskModelTest extends FXTest {
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
    public void testConstructors() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        assertEquals(taskModel, subTaskModel0.getParentTask());
        assertNotNull(subTaskModel0.getId());
        var subTaskModel1 = subTaskFactory.create(taskModel, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals("00000000-0000-0000-0000-000000000002", subTaskModel1.getId().toString());
        var subTask = mock(SubTask.class);
        var subTaskModel2 = subTaskFactory.create(taskModel, subTask);
        assertEquals(subTask.getId(), subTaskModel2.getId());

    }

    @Test
    public void testCompareToShouldReturnCorrectValue() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        var subTaskModel1 = subTaskFactory.create(taskModel);
        subTaskModel0.setPosition(0);
        subTaskModel1.setPosition(1);
        assertEquals(-1, subTaskModel0.compareTo(subTaskModel1));
        assertEquals(1, subTaskModel1.compareTo(subTaskModel0));
        subTaskModel0.setPosition(1);
        subTaskModel1.setPosition(0);
        assertEquals(1, subTaskModel0.compareTo(subTaskModel1));
        assertEquals(-1, subTaskModel1.compareTo(subTaskModel0));
        subTaskModel0.setPosition(0);
        subTaskModel1.setPosition(0);
        assertEquals(0, subTaskModel0.compareTo(subTaskModel1));
        assertEquals(0, subTaskModel1.compareTo(subTaskModel0));
    }

    @Test
    public void testGettersAndSetters() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        subTaskModel0.setCompleted(true);
        assertEquals(true, subTaskModel0.isCompleted());
        subTaskModel0.setCompleted(false);
        assertEquals(false, subTaskModel0.isCompleted());
        assertEquals(false, subTaskModel0.completedProperty().get());
        subTaskModel0.setName("name");
        assertEquals("name", subTaskModel0.getName());
        assertEquals("name", subTaskModel0.nameProperty().get());
        subTaskModel0.setPosition(1);
        assertEquals(1, subTaskModel0.getPosition());
        assertEquals(1, subTaskModel0.positionProperty().get());
        subTaskModel0.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals("00000000-0000-0000-0000-000000000002", subTaskModel0.getId().toString());
        assertEquals("00000000-0000-0000-0000-000000000002", subTaskModel0.idProperty().getValue().toString());
    }

    @Test
    public void testSaveSessionContextShouldUpdateSubTask() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        subTaskModel0.save();
        verify(module.sessionMock).updateSubTask(any(SubTask.class));
    }

    @Test
    public void testRemoveShouldCallParentTaskRemoveSubTask() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        subTaskModel0.delete();
        verify(taskModel).deleteSubtask(subTaskModel0);
    }

    @Test
    public void testPeerUpdatedShouldUpdateSubTask() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        var uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var subTask = new SubTask(uuid, "name", true, 0L);
        subTaskModel0.peerUpdated(subTask);
        assertEquals("name", subTaskModel0.getName());
        assertEquals(true, subTaskModel0.isCompleted());
        assertEquals(0L, subTaskModel0.getPosition());
    }

    @Test
    public void testMoveUpShouldCallParentTaskMoveSubtaskup() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        subTaskModel0.moveUp();
        verify(taskModel).moveSubtaskUp(subTaskModel0);
    }

    @Test
    public void testMoveDownShouldCallParentTaskMoveSubtaskDown() {
        var taskModel = mock(TaskModel.class);
        var subTaskModel0 = subTaskFactory.create(taskModel);
        subTaskModel0.moveDown();
        verify(taskModel).moveSubtaskDown(subTaskModel0);
    }
}
