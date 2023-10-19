package client.models;

import client.utils.FXTest;
import com.github.kiprobinson.bigfraction.BigFraction;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commons.models.Task;
import commons.models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskListModelTest extends FXTest {
    @Inject
    TaskModel.Factory taskFactory;
    @Inject
    TaskListModel.Factory taskListFactory;
    @Inject
    TaskListModel.DummyTaskListModel.Factory dummyTaskListFactory;
    @Inject
    TaskModel.Factory taskModelFactory;
    private Injector injector;
    private FrontendModelTestModule module;

    @BeforeEach
    public void setUp() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @Test
    //test for the constructor in TaskListModel
    public void testConstructor() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);

        assertEquals(boardModel, taskList.getParentBoard());
        assertEquals("", taskList.getTitle());
        assertEquals(BigFraction.ONE_HALF, taskList.getPosition());
    }

    @Test
    //test for the constructor with boardModel and TaskList parameters in TaskListModel
    public void testConstructorWithBoardModelAndTaskList() {
        var boardModel = mock(BoardModel.class);
        var serverTaskList = new TaskList();
        serverTaskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        serverTaskList.setTitle("Test Title");
        serverTaskList.setPosition(BigFraction.ONE_HALF);
        var task = new Task(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Test Title", "Test Description", BigFraction.ONE_HALF);
        serverTaskList.getTasks().add(task);
        var taskList = taskListFactory.create(boardModel, serverTaskList);

        assertEquals(3, taskList.getSortedTasks().size());
        assertEquals(boardModel, taskList.getParentBoard());
        assertEquals("Test Title", taskList.getTitle());
        assertEquals(BigFraction.ONE_HALF, taskList.getPosition());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), taskList.getId());
    }

    @Test
    //test for moveInTaskBefore moveInTask moveInTaskAfter method in TaskListModel
    public void testMoveInTask() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);

        var task1 = taskFactory.create(taskList);
        var task2 = taskFactory.create(taskList);
        var task3 = taskFactory.create(taskList);

        taskList.moveInTask(task2, BigFraction.ONE_HALF);
        taskList.moveInTaskBefore(task1, task2);
        taskList.moveInTaskAfter(task3, task2);
        assertEquals(1, taskList.getSortedTasks().indexOf(task1));
        assertEquals(2, taskList.getSortedTasks().indexOf(task2));
        assertEquals(3, taskList.getSortedTasks().indexOf(task3));
    }

    @Test
    public void testMovInTaskAfterShouldThrowException() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);

        var task1 = taskFactory.create(taskList);
        var task2 = taskFactory.create(taskList);
        var task3 = taskFactory.create(taskList);
        taskList.moveInTask(task1, BigFraction.ONE_HALF);
        assertThrows(IllegalArgumentException.class, () -> taskList.moveInTaskAfter(task1, task1));
        assertThrows(NoSuchElementException.class, () -> taskList.moveInTaskAfter(task3, task2));
    }

    @Test
    public void testMoveInTaskBeforeShouldThrowException() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);

        var task1 = taskFactory.create(taskList);
        var task2 = taskFactory.create(taskList);
        var task3 = taskFactory.create(taskList);
        taskList.moveInTask(task1, BigFraction.ONE_HALF);
        assertThrows(IllegalArgumentException.class, () -> taskList.moveInTaskBefore(task1, task1));
        assertThrows(NoSuchElementException.class, () -> taskList.moveInTaskBefore(task3, task2));
        assertThrows(IllegalArgumentException.class, () -> taskList.moveInTaskBefore(task1, taskList.getFirstDummy()));
    }

    @Test
    //test for createTaskAtEnd createTaskBefore createTaskAfter createTaskBetween method in TaskListModel
    public void testCreateTask() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        Consumer<TaskModel> taskConsumer = mock(Consumer.class);
        var task1 = taskList.createTaskAtEnd(taskConsumer);
        var task2 = taskList.createTaskBefore(task1, taskConsumer);
        var task3 = taskList.createTaskAfter(task1, taskConsumer);
        var task4 = taskList.createTaskBetween(task1, task3, taskConsumer);

        assertEquals(1, taskList.getSortedTasks().indexOf(task2));
        assertEquals(2, taskList.getSortedTasks().indexOf(task1));
        assertEquals(3, taskList.getSortedTasks().indexOf(task4));
        assertEquals(4, taskList.getSortedTasks().indexOf(task3));
    }

    @Test
    public void testCreateTaskAfterShouldThrowException() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        Consumer<TaskModel> taskConsumer = mock(Consumer.class);
        var task1 = taskList.createTaskAtEnd(taskConsumer);
        var task2 = mock(TaskModel.class);
        assertThrows(IllegalArgumentException.class, () -> taskList.createTaskAfter(taskList.getLastDummy(), taskConsumer));
        assertThrows(NoSuchElementException.class, () -> taskList.createTaskAfter(task2, taskConsumer));
    }

    @Test
    public void testCreateTaskBeforeShouldThrowException() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        Consumer<TaskModel> taskConsumer = mock(Consumer.class);
        var task1 = taskList.createTaskAtEnd(taskConsumer);
        var task2 = mock(TaskModel.class);
        assertThrows(IllegalArgumentException.class, () -> taskList.createTaskBefore(taskList.getFirstDummy(), taskConsumer));
        assertThrows(NoSuchElementException.class, () -> taskList.createTaskBefore(task2, taskConsumer));
    }

    @Test
    //test for peerAddedTask method in TaskListModel
    public void testPeerAddedTask() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        var task = mock(TaskModel.class);
        when(task.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        when(task.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        taskList.peerAddedTask(task);
        assertEquals(task, taskList.getTaskById(UUID.fromString("00000000-0000-0000-0000-000000000002")));
        assertThrows(NoSuchElementException.class, () -> taskList.getTaskById(UUID.fromString("00000000-0000-0000-0000-000000000001")));
    }

    @Test
    public void testPeerAddedTaskWithTaskAsParameter() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        var task = new Task(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test", "Test", BigFraction.ONE_HALF);
        taskList.peerAddedTask(task);
        assertEquals("Test", taskList.getTaskById(UUID.fromString("00000000-0000-0000-0000-000000000002")).getDescription());
        assertThrows(NoSuchElementException.class, () -> taskList.getTaskById(UUID.fromString("00000000-0000-0000-0000-000000000001")));
    }

    @Test
    public void testPeerUpdatedTaskShouldUpdateToTaskModel() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        var task = new Task(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Test", "Test", BigFraction.ONE_HALF);
        taskList.peerAddedTask(task);
        task.setDescription("Test2");
        taskList.peerUpdatedTask(task);
        assertEquals("Test2", taskList.getTaskById(UUID.fromString("00000000-0000-0000-0000-000000000002")).getDescription());
    }

    @Test
    public void testSaveShouldUpdateTaskListToSessionContext() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        taskList.save();
        verify(this.module.sessionMock).updateTaskList(taskList.toServerTaskList());
    }

    @Test
    public void testPeerUpdatedShouldSetTaskListNewTitle() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(boardModel);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        var taskList2 = new TaskList(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Test");
        taskList.peerUpdated(taskList2);
        assertEquals("Test", taskList.getTitle());
    }


    // a test that checks whether the constructor initialized the list with 2 dummy nodes correctly
    @Test
    public void testGettersSetters() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        taskList.setTitle("Test Title");
        taskList.setPosition(BigFraction.ONE_HALF);
        taskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        assertEquals(boardModel, taskList.getParentBoard());

        taskList.setParentBoard(null);
        assertNull(taskList.getParentBoard());
        assertEquals("Test Title", taskList.getTitle());
        assertEquals(BigFraction.ONE_HALF, taskList.getPosition());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), taskList.getId());

        var expectedServerTaskList = new TaskList();
        expectedServerTaskList.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        expectedServerTaskList.setTitle("Test Title");
        expectedServerTaskList.setPosition(BigFraction.ONE_HALF);
        assertEquals(expectedServerTaskList, taskList.toServerTaskList());
    }


    @Test
    //test for the titleProperty parentBoardProperty positionProperty idProperty methods
    public void testPropertyMethods() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        assertEquals("", taskList.titleProperty().get());
        assertEquals(boardModel, taskList.parentBoardProperty());
        assertEquals(BigFraction.ONE_HALF, taskList.positionProperty().get());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), taskList.idProperty().get());

        taskList.titleProperty().set("Test Title");
        taskList.setParentBoard(null);
        taskList.positionProperty().set(BigFraction.ONE_HALF);
        taskList.idProperty().set(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        assertEquals("Test Title", taskList.titleProperty().get());
        assertNull(taskList.getParentBoard());
        assertEquals(BigFraction.ONE_HALF, taskList.positionProperty().get());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), taskList.idProperty().get());
    }

    @Test
    //test compareTo method in TaskListModel
    public void testCompareTo() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        var taskList2 = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000003")
        );

        taskList.setPosition(BigFraction.ONE_HALF);
        taskList2.setPosition(BigFraction.ONE);

        assertEquals(-1, taskList.compareTo(taskList2));
        assertEquals(1, taskList2.compareTo(taskList));
        assertEquals(0, taskList.compareTo(taskList));
    }

    @Test
    //test remove method in TaskListModel
    public void testRemove() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        taskList.remove();

        verify(boardModel).removeTaskList(taskList);
    }

    @Test
    //test getDraggedCardIndex and draggedCardIndexProperty method in TaskListModel
    public void testGetDraggedCardIndex() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        assertEquals(-2, taskList.getDraggedCardIndex());
        assertEquals(-2, taskList.draggedCardIndexProperty().get());
    }

    @Test
    //test equals and hashCode method in TaskListModel
    public void testEqualsAndHashCode() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        var taskList2 = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000003")
        );

        assertEquals(taskList, taskList);
        assertNotEquals(taskList, taskList2);
        assertNotEquals(taskList, null);
        assertNotEquals(taskList, new Object());
        assertEquals(taskList.hashCode(), taskList.hashCode());
        assertNotEquals(taskList.hashCode(), taskList2.hashCode());
    }

    @Test
    //test isDummy method in TaskListModel
    public void testIsDummy() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        assertFalse(taskList.isDummy());
    }


    @Test
    public void testConstructorAddedDummyNodes() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        assertEquals(2, taskList.getSortedTasks().size());
        assertEquals(BigFraction.ZERO, taskList.getSortedTasks().get(0).getPosition());
        assertEquals(BigFraction.ONE, taskList.getSortedTasks().get(1).getPosition());
        assertTrue(taskList.getSortedTasks().get(0).isDummy());
        assertTrue(taskList.getSortedTasks().get(1).isDummy());
    }

    @Test
    public void createTaskBetweenSameItemsShouldThrowException() {
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        assertThrows(IllegalArgumentException.class, () -> {
            taskList.createTaskBetween(taskList.getLastDummy(), taskList.getLastDummy(), n -> {

            });
        });
    }

    @Test
    public void createTaskBetweenDifferentTaskListTasksShouldThrowException() {
        // init task list
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );


        // they have different task list ids
        var mockTaskList1 = mock(TaskListModel.class);
        var mockTaskList2 = mock(TaskListModel.class);
        when(mockTaskList1.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockTaskList2.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        var task1 = taskFactory.create(mockTaskList1);
        task1.setPosition(new BigFraction(1, 3));

        var task2 = taskFactory.create(mockTaskList2);
        task2.setPosition(new BigFraction(2, 3));

        // should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            taskList.createTaskBetween(task1, task2, n -> {
            });
        });
    }

    @Test
    public void createTaskBetweenTasksInSameTaskListShouldWork() {
        // Initializing task list
        var boardModel = mock(BoardModel.class);
        var taskList = taskListFactory.create(
                boardModel,
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );

        // Inserting first task
        taskList.createTaskBetween(taskList.getFirstDummy(), taskList.getLastDummy(), n -> {
            n.setTitle("Test Title");
        });

        assertEquals(3, taskList.getSortedTasks().size());

        assertEquals(BigFraction.ZERO, taskList.getSortedTasks().get(0).getPosition());
        assertEquals(new BigFraction(1, 2), taskList.getSortedTasks().get(1).getPosition());
        assertEquals(BigFraction.ONE, taskList.getSortedTasks().get(2).getPosition());

        assertTrue(taskList.getSortedTasks().get(0).isDummy());
        assertFalse(taskList.getSortedTasks().get(1).isDummy());

        assertTrue(taskList.getSortedTasks().get(2).isDummy());

        assertEquals("Test Title", taskList.getSortedTasks().get(1).getTitle(), "Initializer method should have been called");
        assertEquals(1, taskList.taskCount());
        assertEquals("Test Title", taskList.getLastTask().getTitle());

        // Inserting second task
        taskList.createTaskBetween(taskList.getFirstDummy(), taskList.getSortedTasks().get(1), n -> {
            n.setTitle("Test Title 2");
        });

        assertEquals(4, taskList.getSortedTasks().size());

        assertEquals(BigFraction.ZERO, taskList.getSortedTasks().get(0).getPosition());
        assertEquals(new BigFraction(1, 4), taskList.getSortedTasks().get(1).getPosition());
        assertEquals(new BigFraction(1, 2), taskList.getSortedTasks().get(2).getPosition());
        assertEquals(BigFraction.ONE, taskList.getSortedTasks().get(3).getPosition());

        assertTrue(taskList.getSortedTasks().get(0).isDummy());
        assertFalse(taskList.getSortedTasks().get(1).isDummy());
        assertFalse(taskList.getSortedTasks().get(2).isDummy());
        assertTrue(taskList.getSortedTasks().get(3).isDummy());

        assertEquals("Test Title 2", taskList.getSortedTasks().get(1).getTitle(), "Wrong order of tasks");
        assertEquals("Test Title", taskList.getSortedTasks().get(2).getTitle(), "Wrong order of tasks");
    }


    @Test
    public void onDroppedMovesCardsCorrectlyWithinListToTheEnd() throws InterruptedException {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockBoard.getListById(any(UUID.class))).thenReturn(taskList);

        var task1 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 1");
        });

        var task2 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 2");
        });

        var task3 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 3");
        });

        // assume task1 is dragged after task3

        taskList.dragoverPosProperty().set(4);


        CountDownLatch latch = new CountDownLatch(1);
        task1.setRequestFocusCallback(latch::countDown);

        taskList.onCardDropped(taskList.getId() + " " + task1.getId());

        assertEquals(5, taskList.getSortedTasks().size());
        assertEquals(BigFraction.ZERO, taskList.getSortedTasks().get(0).getPosition());
        assertEquals(BigFraction.ONE, taskList.getSortedTasks().get(4).getPosition());

        assertEquals("Test Title 2", taskList.getSortedTasks().get(1).getTitle(), "Wrong order of tasks");
        assertEquals("Test Title 3", taskList.getSortedTasks().get(2).getTitle(), "Wrong order of tasks");
        assertEquals("Test Title 1", taskList.getSortedTasks().get(3).getTitle(), "Wrong order of tasks");


        assertEquals(-1, taskList.dragoverPosProperty().get());

        // expect that the callback has been called after the drop.
        assertTrue(latch.await(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testOnCardDroppedReturnFalseWhenContentSplitLengthNotEqualsTwo() {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));

        assertFalse(taskList.onCardDropped("1"));
    }

    @Test
    public void onDroppedMovesCardsCorrectly() {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockBoard.getListById(any(UUID.class))).thenReturn(taskList);

        var task1 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 1");
        });

        var task2 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 2");
        });

        var task3 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 3");
        });

        taskList.onCardDropped(taskList.getId() + " " + task1.getId());
        assertEquals(task1, taskList.getSortedTasks().get(taskList.taskCount()));

    }

    @Test
    public void onDroppedMovesCardsCorrectlyWithinListToTheBeginning() {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockBoard.getListById(any(UUID.class))).thenReturn(taskList);

        var task1 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 1");
        });

        var task2 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 2");
        });

        var task3 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 3");
        });

        // assume task1 is dragged after task3

        taskList.dragoverPosProperty().set(1);
        taskList.onCardDropped(taskList.getId() + " " + task3.getId());

        assertEquals(5, taskList.getSortedTasks().size());
        assertEquals(BigFraction.ZERO, taskList.getSortedTasks().get(0).getPosition());
        assertEquals(BigFraction.ONE, taskList.getSortedTasks().get(4).getPosition());

        assertEquals("Test Title 3", taskList.getSortedTasks().get(1).getTitle(), "Wrong order of tasks");
        assertEquals("Test Title 1", taskList.getSortedTasks().get(2).getTitle(), "Wrong order of tasks");
        assertEquals("Test Title 2", taskList.getSortedTasks().get(3).getTitle(), "Wrong order of tasks");

        assertEquals(-1, taskList.dragoverPosProperty().get());

    }

    @Test
    public void testRemoveTaskShouldRemoveFromObservableList() {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockBoard.getListById(any(UUID.class))).thenReturn(taskList);

        var task1 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 1");
        });

        var task2 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 2");
        });

        var task3 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 3");
        });

        assertEquals(5, taskList.getSortedTasks().size());

        taskList.removeTask(task2);
        verify(this.module.sessionMock).removeTask(task2.getId(), task2.getParentTaskList().getId());

        assertEquals(4, taskList.getSortedTasks().size());
        assertEquals("Test Title 1", taskList.getSortedTasks().get(1).getTitle());
        assertEquals("Test Title 3", taskList.getSortedTasks().get(2).getTitle());
    }

    @Test
    public void testPeerRemovedTaskShouldRemoveFromObservableList() {
        var mockBoard = mock(BoardModel.class);
        var taskList = taskListFactory.create(mockBoard, UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockBoard.getListById(any(UUID.class))).thenReturn(taskList);

        var task1 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 1");
        });

        var task2 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 2");
        });

        var task3 = taskList.createTaskBefore(taskList.getLastDummy(), n -> {
            n.setTitle("Test Title 3");
        });

        assertEquals(5, taskList.getSortedTasks().size());
        AtomicInteger number = new AtomicInteger(1);
        task2.setOnDelete(() -> {
            number.set(2);
        });
        taskList.peerRemovedTask(task2);
        assertEquals(2, number.get());
    }


    @Test
    //test for DummyTaskListModel constructor and isdummy method
    public void testDummyTaskListModel() {
        var mockBoard = mock(BoardModel.class);
        var dummyTaskListModel = dummyTaskListFactory.create(mockBoard, BigFraction.ONE);
        assertNotNull(dummyTaskListModel);
        assertTrue(dummyTaskListModel.isDummy());
    }
}