package server.services;

import commons.messages.bidirectional.*;
import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import server.models.BoardSession;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class SessionService implements ApplicationListener<SessionDisconnectEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    // Maintains a mapping of boards' joinKey => BoardSession (what board has what board session)
    private final Map<String, BoardSession> boardSessions;

    // Maintains a mapping of users' sessionId => BoardSession (what user is connected to what board session)
    private final Map<String, BoardSession> userSessions;

    private BoardService boardService;

    @Autowired
    public SessionService(SimpMessagingTemplate messagingTemplate, BoardService boardService) {
        this.messagingTemplate = messagingTemplate;
        this.boardService = boardService;

        this.boardSessions = new HashMap<>();
        this.userSessions = new HashMap<>();
    }

    private BoardSession retrieveSession(String sessionId) {
        var session = userSessions.get(sessionId);
        if (session == null) System.out.printf("[BoardService] Operation failed, user (%s) is not part of a session%n", sessionId);

        return session;
    }

    public void closeSession(BoardSession session) {
        for (var user : session.getUsers())
            userSessions.remove(user);

        boardSessions.remove(session.getBoard().getKey());
        System.out.printf("[Board Service] Removing board session %s for deleted board%n", session.getBoard().getKey());
    }

    /**
     * Adds a user to a board session. If the session for the specified board doesn't exist one is created
     *
     * @param sessionId - the session id of the user
     * @param message   - the JoinC2SMessage containing the board join key and name of the user joining the session
     */
    public void join(String sessionId, JoinC2SMessage message) {
        // Quit any existing sessions
        quit(sessionId, null);

        BoardSession boardSession;

        if (!boardSessions.containsKey(message.getBoardKey())) {
            var board = boardService.findBoardWithAssociations(message.getBoardKey());
            if (board.isEmpty()) {
                System.out.printf("[Board Service] Board with key %s doesn't exist%n", message.getBoardKey());
                throw new NoSuchElementException("Board with key " + message.getBoardKey() + " doesn't exist");
            }

            boardSession = new BoardSession(board.get());
            boardSessions.put(message.getBoardKey(), boardSession);

            System.out.printf("[Board Service] Creating board session %s%n", boardSession.getBoard().getKey());
        } else {
            boardSession = boardSessions.get(message.getBoardKey());
        }

        boardSession.join(sessionId, message);
        userSessions.put(sessionId, boardSession);
    }

    /**
     * Removes a user from the board session they are part of. If the board session remains empty it is deleted
     *
     * @param sessionId - the session id of the user
     * @param message   - the QuitC2SMessage message
     */
    public void quit(String sessionId, QuitC2SMessage message) {
        var boardSession = userSessions.get(sessionId);
        if (boardSession != null) {
            boardSession.quit(sessionId, message);
            userSessions.remove(sessionId);

            if (boardSession.isEmpty()) {
                boardSessions.remove(boardSession.getBoard().getKey());
                System.out.printf("[Board Service] Removing empty board session %s%n", boardSession.getBoard().getKey());
            }
        }
    }

    public void addTask(String sessionId, AddTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.addTask(sessionId, message);
    }

    public void updateTask(String sessionId, UpdateTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateTask(sessionId, message);
    }

    public void moveTask(String sessionId, MoveTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.moveTask(sessionId, message);
    }

    public void removeTask(String sessionId, RemoveTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeTask(sessionId, message);
    }

    public void addTaskList(String sessionId, AddTaskListMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.addTaskList(sessionId, message);
    }

    public void updateTaskList(String sessionId, UpdateTaskListMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateTaskList(sessionId, message);
    }

    public void removeTaskList(String sessionId, RemoveTaskListMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeTaskList(sessionId, message);
    }

    public void updateBoard(String sessionId, UpdateBoardMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateBoard(sessionId, message);
    }

    public void removeBoard(String sessionId, RemoveBoardMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeBoard(sessionId, message);
    }

    public void addTag(String sessionId, AddTagMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.addTag(sessionId, message);
    }

    public void updateTag(String sessionId, UpdateTagMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateTag(sessionId, message);
    }

    public void removeTag(String sessionId, RemoveTagMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeTag(sessionId, message);
    }

    public void addTagToTask(String sessionId, AddTagToTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.addTagToTask(sessionId, message);
    }

    public void removeTagFromTask(String sessionId, RemoveTagFromTaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeTagFromTask(sessionId, message);
    }
    public void createSubtask(String sessionId, CreateSubtaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.createSubtask(sessionId, message);
    }

    public void updateSubtask(String sessionId, UpdateSubtaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateSubtask(sessionId, message);
    }

    public void swapSubtaskPosition(String sessionId, SwapSubtaskPositionMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.swapSubtaskPosition(sessionId, message);
    }

    public void removeSubtask(String sessionId, RemoveSubtaskMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeSubtask(sessionId, message);
    }

    public void createHighlight(String sessionId, CreateHighlightMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.createHighlight(sessionId, message);
    }

    public void updateHighlight(String sessionId, UpdateHighlightMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.updateHighlight(sessionId, message);
    }

    public void removeHighlight(String sessionId, RemoveHighlightMessage message) {
        var boardSession = retrieveSession(sessionId);
        if (boardSession != null) boardSession.removeHighlight(sessionId, message);
    }

    /**
     * Send a private message to a user
     *
     * @param sessionId - the session id of the user
     * @param message   - the message to be sent
     */
    public void send(String sessionId, commons.messages.Message message) {
        String destination = "/topic/" + commons.messages.Message.getTopic(message.getClass());

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(sessionId, destination, message, headerAccessor.getMessageHeaders());

        System.out.printf("[Websocket] Sending a message to user %s of type %s at %s%n", sessionId, message.getClass().getSimpleName(), destination);
    }

    /**
     * Called when a user disconnects from a WebSocket session. Removes the user from their board session.
     *
     * @param event the disconnect event to respond to
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        System.out.printf("[BoardService] User session %s disconnected%n", event.getSessionId());

        quit(event.getSessionId(), null);
    }
}
