package server.controllers;

import commons.messages.bidirectional.*;
import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import server.services.SessionService;

@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @MessageMapping("/join-c2s-message")
    public void join(@Payload JoinC2SMessage message, @Header("sessionId") String sessionId) {
        sessionService.join(sessionId, message);
    }

    @MessageMapping("/quit-c2s-message")
    public void quit(@Payload QuitC2SMessage message, @Header("sessionId") String sessionId) {
        System.out.println("Quit message received");
        sessionService.quit(sessionId, message);
    }

    @MessageMapping("/add-task-message")
    public void addTask(@Payload AddTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.addTask(sessionId, message);
    }

    @MessageMapping("/update-task-message")
    public void updateTask(@Payload UpdateTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateTask(sessionId, message);
    }

    @MessageMapping("/move-task-message")
    public void moveTask(@Payload MoveTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.moveTask(sessionId, message);
    }

    @MessageMapping("/remove-task-message")
    public void removeTask(@Payload RemoveTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeTask(sessionId, message);
    }

    @MessageMapping("/add-task-list-message")
    public void addTaskList(@Payload AddTaskListMessage message, @Header("sessionId") String sessionId) {
        sessionService.addTaskList(sessionId, message);
    }

    @MessageMapping("/update-task-list-message")
    public void updateTaskList(@Payload UpdateTaskListMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateTaskList(sessionId, message);
    }

    @MessageMapping("/remove-task-list-message")
    public void removeTaskList(@Payload RemoveTaskListMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeTaskList(sessionId, message);
    }

    @MessageMapping("/update-board-message")
    public void updateBoard(@Payload UpdateBoardMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateBoard(sessionId, message);
    }

    @MessageMapping("/remove-board-message")
    public void removeBoard(@Payload RemoveBoardMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeBoard(sessionId, message);
    }

    @MessageMapping("/add-tag-message")
    public void addTag(@Payload AddTagMessage message, @Header("sessionId") String sessionId) {
        sessionService.addTag(sessionId, message);
    }

    @MessageMapping("/add-tag-to-task-message")
    public void addTagToTask(@Payload AddTagToTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.addTagToTask(sessionId, message);
    }

    @MessageMapping("/remove-tag-from-task-message")
    public void removeTagFromTask(@Payload RemoveTagFromTaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeTagFromTask(sessionId, message);
    }

    @MessageMapping("/update-tag-message")
    public void updateTag(@Payload UpdateTagMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateTag(sessionId, message);
    }

    @MessageMapping("/remove-tag-message")
    public void removeTag(@Payload RemoveTagMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeTag(sessionId, message);
    }

    @MessageMapping("/create-subtask-message")
    public void createSubtask(@Payload CreateSubtaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.createSubtask(sessionId, message);
    }

    @MessageMapping("/update-subtask-message")
    public void updateSubtask(@Payload UpdateSubtaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateSubtask(sessionId, message);
    }

    @MessageMapping("/swap-subtask-position-message")
    public void swapSubtaskPosition(@Payload SwapSubtaskPositionMessage message, @Header("sessionId") String sessionId) {
        sessionService.swapSubtaskPosition(sessionId, message);
    }

    @MessageMapping("/remove-subtask-message")
    public void removeSubtask(@Payload RemoveSubtaskMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeSubtask(sessionId, message);
    }

    @MessageMapping("/create-highlight-message")
    public void createHighlight(@Payload CreateHighlightMessage message, @Header("sessionId") String sessionId) {
        sessionService.createHighlight(sessionId, message);
    }

    @MessageMapping("/update-highlight-message")
    public void saveHighlight(@Payload UpdateHighlightMessage message, @Header("sessionId") String sessionId) {
        sessionService.updateHighlight(sessionId, message);
    }

    @MessageMapping("/remove-highlight-message")
    public void removeHighlight(@Payload RemoveHighlightMessage message, @Header("sessionId") String sessionId) {
        sessionService.removeHighlight(sessionId, message);
    }
}
