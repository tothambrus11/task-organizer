package server.controllers;

import commons.messages.bidirectional.*;
import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.services.SessionService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionService sessionService;

    private String sessionId;

    @BeforeEach
    public void setUp() {
        sessionId = "test-session-id";
    }

    @Test
    public void joinTest() {
        JoinC2SMessage message = new JoinC2SMessage();
        sessionController.join(message, sessionId);
        verify(sessionService, times(1)).join(sessionId, message);
    }

    @Test
    public void quitTest() {
        QuitC2SMessage message = new QuitC2SMessage();
        sessionController.quit(message, sessionId);
        verify(sessionService, times(1)).quit(sessionId, message);
    }

    @Test
    public void addTaskTest() {
        AddTaskMessage message = new AddTaskMessage();
        sessionController.addTask(message, sessionId);
        verify(sessionService, times(1)).addTask(sessionId, message);
    }

    @Test
    public void updateTaskTest() {
        UpdateTaskMessage message = new UpdateTaskMessage();
        sessionController.updateTask(message, sessionId);
        verify(sessionService, times(1)).updateTask(sessionId, message);
    }

    @Test
    public void moveTaskTest() {
        MoveTaskMessage message = new MoveTaskMessage();
        sessionController.moveTask(message, sessionId);
        verify(sessionService, times(1)).moveTask(sessionId, message);
    }

    @Test
    public void removeTaskTest() {
        RemoveTaskMessage message = new RemoveTaskMessage();
        sessionController.removeTask(message, sessionId);
        verify(sessionService, times(1)).removeTask(sessionId, message);
    }

    @Test
    public void addTaskListTest() {
        AddTaskListMessage message = new AddTaskListMessage();
        sessionController.addTaskList(message, sessionId);
        verify(sessionService, times(1)).addTaskList(sessionId, message);
    }

    @Test
    public void updateTaskListTest() {
        UpdateTaskListMessage message = new UpdateTaskListMessage();
        sessionController.updateTaskList(message, sessionId);
        verify(sessionService, times(1)).updateTaskList(sessionId, message);
    }

    @Test
    public void removeTaskListTest() {
        RemoveTaskListMessage message = new RemoveTaskListMessage();
        sessionController.removeTaskList(message, sessionId);
        verify(sessionService, times(1)).removeTaskList(sessionId, message);
    }

    @Test
    public void updateBoardTest() {
        UpdateBoardMessage message = new UpdateBoardMessage();
        sessionController.updateBoard(message, sessionId);
        verify(sessionService, times(1)).updateBoard(sessionId, message);
    }

    @Test
    public void removeBoardTest() {
        RemoveBoardMessage message = new RemoveBoardMessage();
        sessionController.removeBoard(message, sessionId);
        verify(sessionService, times(1)).removeBoard(sessionId, message);
    }
}