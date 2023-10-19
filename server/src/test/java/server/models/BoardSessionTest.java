package server.models;

import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import commons.messages.s2c.InitBoardStateS2CMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import commons.models.*;
import server.database.*;
import server.services.*;


@ExtendWith(MockitoExtension.class)
public class BoardSessionTest {
    @Mock
    private SessionService sessionService;

    @Mock
    private RefreshService refreshService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @Mock
    private SubTaskRepository subTaskRepository;

    @Mock
    private HighlightRepository highlightRepository;

    @InjectMocks
    private BoardSession boardSession;

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
        boardSession = new BoardSession(board, sessionService, refreshService, boardRepository, taskRepository, tagRepository, taskListRepository, subTaskRepository, highlightRepository);
    }

    @Test
    void testJoin() {
        String sessionId = "1";
        String username = "TestUser";
        JoinC2SMessage joinMessage = new JoinC2SMessage();

        joinMessage.setUsername(username);

        boardSession.join(sessionId, joinMessage);

        verify(sessionService, times(1)).send(eq(sessionId), any(InitBoardStateS2CMessage.class));
        verify(sessionService).send(eq("1"), any(InitBoardStateS2CMessage.class));

    }

    @Test
    void testQuit() {
        String sessionId = "1";
        JoinC2SMessage joinMessage = new JoinC2SMessage();

        joinMessage.setUsername("TestUser");

        boardSession.join(sessionId, joinMessage);

        QuitC2SMessage quitMessage = new QuitC2SMessage();

        boardSession.quit(sessionId, quitMessage);

        verify(sessionService).send(eq("1"), any(InitBoardStateS2CMessage.class));

    }


}