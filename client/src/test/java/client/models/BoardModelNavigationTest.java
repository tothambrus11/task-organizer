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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardModelNavigationTest extends FXTest {
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

        var mockHighlight = mock(HighlightModel.class);
        when(mockHighlight.getId()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        boardModel.defaultHighlightProperty().set(mockHighlight);

        l1 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 1"));
        l2 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 2"));
        l3 = boardModel.createTaskListAtEnd(l -> l.setTitle("List 3"));

        t1l1 = l1.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 1 in List 1");
        });
        t2l1 = l1.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 2 in List 1");
        });
        t3l1 = l1.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 3 in List 1");
        });

        t1l2 = l2.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 1 in List 2");
        });
        t2l2 = l2.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 2 in List 2");
        });
        t3l2 = l2.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 3 in List 2");
        });

        t1l3 = l3.createTaskAtEnd(t -> {
            t.setHighlight(boardModel.getDefaultHighlight());
            t.setTitle("Task 1 in List 3");
        });
    }

    @Test
    public void navigateDownRegularly1() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.DOWN, 1, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }


    @Test
    public void navigateDownRegularly2() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t3l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.DOWN, 1, 2);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateDownAtTheEndOfList() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l2.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.DOWN, 1, 3);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateDownAtTheEndOfBoard() throws InterruptedException {
        var latch = new CountDownLatch(1);
        l3.getSortedTasks().get(2).setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.DOWN, 3, 1);
        assertFalse(latch.await(10, TimeUnit.MILLISECONDS)); // Should not be called
    }

    @Test
    public void navigateUpWorks() throws InterruptedException {
        {
            var latch = new CountDownLatch(1);
            t2l1.setRequestFocusCallback(latch::countDown);
            boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 3);
            assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
        }

        {
            var latch = new CountDownLatch(1);
            t1l1.setRequestFocusCallback(latch::countDown);
            boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 2);
            assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
        }

        {
            var latch = new CountDownLatch(1);
            l1.getSortedTasks().get(0).setRequestFocusCallback(latch::countDown);
            boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 1);
            assertFalse(latch.await(10, TimeUnit.MILLISECONDS));
        }

        {
            var latch = new CountDownLatch(1);
            t3l2.setRequestFocusCallback(latch::countDown);
            boardModel.navigate(BoardModel.NavigationDirection.UP, 3, 1);
            assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
        }
    }

    // refactor to individual tests:

    @Test
    public void navigateUpRegularly1() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 3);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateUpRegularly2() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 2);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateUpAtTheBeginningOfList() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t3l2.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.UP, 3, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateUpAtTheBeginningOfBoard() throws InterruptedException {
        var latch = new CountDownLatch(1);
        l1.getSortedTasks().get(0).setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.UP, 1, 1);
        assertFalse(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateRightRegularly() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l2.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.RIGHT, 1, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateRightRegularly2() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l3.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.RIGHT, 2, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateRightAtLastList() throws InterruptedException {
        var t2l3 = l3.createTaskAtEnd(t -> t.setTitle("Task 2 in List 3"));
        var t3l3 = l3.createTaskAtEnd(t -> t.setTitle("Task 3 in List 3"));

        var latch = new CountDownLatch(1);
        t3l3.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.RIGHT, 3, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateRightToShorterList() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l3.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.RIGHT, 2, 3);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateLeftRegularly() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t2l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.LEFT, 2, 2);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateLeftRegularly2() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l2.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.LEFT, 3, 1);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateLeftAtFirstList() throws InterruptedException {
        var latch = new CountDownLatch(1);
        t1l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.LEFT, 1, 3);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateLeftAtFirstCard() throws InterruptedException {
        var latch = new CountDownLatch(1);
        l1.getSortedTasks().get(0).setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.LEFT, 1, 1);
        assertFalse(latch.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void navigateLeftToShorterList() throws InterruptedException {
        var t4l2 = l2.createTaskAtEnd(t -> t.setTitle("Task 4 in List 2"));
        var t5l2 = l2.createTaskAtEnd(t -> t.setTitle("Task 5 in List 2"));

        var latch = new CountDownLatch(1);
        t3l1.setRequestFocusCallback(latch::countDown);
        boardModel.navigate(BoardModel.NavigationDirection.LEFT, 2, 5);
        assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
    }

}
