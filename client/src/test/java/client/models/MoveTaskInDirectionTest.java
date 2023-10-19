package client.models;

import client.utils.FXTest;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class MoveTaskInDirectionTest extends FXTest {
    @Inject
    TaskModel.Factory taskFactory;
    @Inject
    TaskListModel.Factory taskListFactory;
    @Inject
    BoardModel.Factory boardFactory;
    private Injector injector;
    private FrontendModelTestModule module;
    private TaskListModel l1;
    private TaskListModel l2;
    private TaskListModel l3;
    private TaskModel t1l1;
    private TaskModel t2l1;
    private TaskModel t3l1;
    private TaskModel t1l2;
    private TaskModel t2l2;
    private TaskModel t3l2;
    private TaskModel t1l3;
    private BoardModel boardModel;

    @BeforeAll
    public void setUpTestSuite() {
        module = new FrontendModelTestModule(); // initializes the mock session context
        injector = Guice.createInjector(module);
        injector.injectMembers(this);
    }

    @BeforeEach
    public void setUpTest() {
        boardModel = boardFactory.create(UUID.randomUUID());

        l1 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 1"));
        l2 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 2"));
        l3 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 3"));

        t1l1 = l1.createTaskAtEnd(t -> t.setTitle("Task 1 in List 1"));
        t2l1 = l1.createTaskAtEnd(t -> t.setTitle("Task 2 in List 1"));
        t3l1 = l1.createTaskAtEnd(t -> t.setTitle("Task 3 in List 1"));

        t1l2 = l2.createTaskAtEnd(t -> t.setTitle("Task 1 in List 2"));
        t2l2 = l2.createTaskAtEnd(t -> t.setTitle("Task 2 in List 2"));
        t3l2 = l2.createTaskAtEnd(t -> t.setTitle("Task 3 in List 2"));

        t1l3 = l3.createTaskAtEnd(t -> t.setTitle("Task 1 in List 3"));
    }

    /**
     * docs for the method that is tested:
     * Moves the task to the direction specified, from the given list and task index.
     * UP/DOWN will move the task in the list, LEFT/RIGHT will move the task to the next list's end when possible.
     * If the move wouldn't be possible, it doesn't do anything, doesn't even set the task to be focused.
     *
     */


    @Test
    public void moveTaskDownNormally() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.DOWN, 1, 2);
        assertTrue(latch.await(1, TimeUnit.MILLISECONDS), "Request focus callback is not called");
        assertEquals("Task 1 in List 1", l1.getSortedTasks().get(1).getTitle(), "Task is not moved correctly");
        assertEquals("Task 3 in List 1", l1.getSortedTasks().get(2).getTitle(), "Task is not moved correctly");
        assertEquals("Task 2 in List 1", l1.getSortedTasks().get(3).getTitle(), "Task is not moved correctly");
    }

    @Test
    public void moveTaskDownAtTheEnd() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t3l2.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.DOWN, 2, 3);
        assertFalse(latch.await(1, TimeUnit.MILLISECONDS), "Request focus shouldn't be called");
        assertEquals("Task 1 in List 2", l2.getSortedTasks().get(1).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 2 in List 2", l2.getSortedTasks().get(2).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 3 in List 2", l2.getSortedTasks().get(3).getTitle(), "Task shouldn't be moved.");
    }

    @Test
    public void moveTaskUpNormally() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l2.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.UP, 2, 2);
        assertTrue(latch.await(1, TimeUnit.MILLISECONDS), "Request focus callback should be called");
        assertEquals("Task 2 in List 2", l2.getSortedTasks().get(1).getTitle(), "Task isn't moved correctly");
        assertEquals("Task 1 in List 2", l2.getSortedTasks().get(2).getTitle(), "Task isn't moved correctly");
        assertEquals("Task 3 in List 2", l2.getSortedTasks().get(3).getTitle(), "Task isn't moved correctly");
    }

    @Test
    public void moveTaskUpAtTheBeginning() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l2.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.UP, 2, 1);
        assertFalse(latch.await(1, TimeUnit.MILLISECONDS), "Request focus shouldn't be called");
        assertEquals("Task 1 in List 2", l2.getSortedTasks().get(1).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 2 in List 2", l2.getSortedTasks().get(2).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 3 in List 2", l2.getSortedTasks().get(3).getTitle(), "Task shouldn't be moved.");
    }

    @Test
    public void moveTaskLeftNormally() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l2.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.LEFT, 2, 2);
        assertTrue(latch.await(1, TimeUnit.MILLISECONDS), "Request focus callback is not called");
        assertEquals("Task 1 in List 1", l1.getSortedTasks().get(1).getTitle(), "Task is not moved correctly");
        assertEquals("Task 2 in List 1", l1.getSortedTasks().get(2).getTitle(), "Task is not moved correctly");
        assertEquals("Task 3 in List 1", l1.getSortedTasks().get(3).getTitle(), "Task is not moved correctly");
        assertEquals("Task 2 in List 2", l1.getSortedTasks().get(4).getTitle(), "Task is not moved correctly");
        assertEquals("Task 1 in List 2", l2.getSortedTasks().get(1).getTitle(), "Task is not moved correctly");
        assertEquals("Task 3 in List 2", l2.getSortedTasks().get(2).getTitle(), "Task is not moved correctly");
        assertEquals(l2.getLastDummy(), l2.getSortedTasks().get(3), "The third should be removed (it should be the dummy task)");
    }

    @Test
    public void moveTaskLeftAtTheBeginning() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.LEFT, 1, 2);
        assertFalse(latch.await(1, TimeUnit.MILLISECONDS), "Request focus shouldn't be called");
        assertEquals("Task 1 in List 1", l1.getSortedTasks().get(1).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 2 in List 1", l1.getSortedTasks().get(2).getTitle(), "Task shouldn't be moved.");
        assertEquals("Task 3 in List 1", l1.getSortedTasks().get(3).getTitle(), "Task shouldn't be moved.");
    }

    @Test
    public void moveTaskRightNormally() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.moveTaskInDirection(BoardModel.NavigationDirection.RIGHT, 1, 2);
        assertTrue(latch.await(1, TimeUnit.MILLISECONDS), "Request focus callback is not called");

        assertEquals("Task 1 in List 1", l1.getSortedTasks().get(1).getTitle(), "Task is not moved correctly");
        assertEquals("Task 3 in List 1", l1.getSortedTasks().get(2).getTitle(), "Task is not moved correctly");
        assertEquals(l1.getLastDummy(), l1.getSortedTasks().get(3), "Task should be the dummy node");

        assertEquals("Task 1 in List 2", l2.getSortedTasks().get(1).getTitle(), "Task is not moved correctly");
        assertEquals("Task 2 in List 2", l2.getSortedTasks().get(2).getTitle(), "Task is not moved correctly");
        assertEquals("Task 3 in List 2", l2.getSortedTasks().get(3).getTitle(), "Task is not moved correctly");
        assertEquals("Task 2 in List 1", l2.getSortedTasks().get(4).getTitle(), "Task is not moved correctly");

    }
}

