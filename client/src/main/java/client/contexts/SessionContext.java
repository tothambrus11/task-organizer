package client.contexts;

import client.models.BoardModel;
import client.utils.ServerUtils;
import com.github.kiprobinson.bigfraction.BigFraction;
import commons.messages.Message;
import commons.messages.bidirectional.*;
import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import commons.messages.s2c.InitBoardStateS2CMessage;
import commons.models.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Component
public class SessionContext {
    private static final Logger logger = Logger.getLogger("BoardContext");

    private final UserContext userContext;
    private final ServerUtils server;

    private final BoardModel.Factory boardModelFactory;
    private BoardModel boardModel;
    private Consumer<BoardModel> onModelInitialized = (board) -> {
    };
    private List<StompSession.Subscription> subscriptions = List.of();

    @Inject
    public SessionContext(UserContext userContext, ServerUtils server, BoardModel.Factory boardModelFactory) {
        this.userContext = userContext;
        this.server = server;
        this.boardModelFactory = boardModelFactory;
    }

    public void sendMessage(Message message) {
        logger.info("Sending message: %s%n".formatted(message.getClass().getName()));
        server.send(message);
    }

    public void addTask(Task task) {
        logger.info("Adding a task %s with id %s%n".formatted(task.getTitle(), task.getId()));
        server.send(new AddTaskMessage(task));
    }

    public void updateTask(Task task) {
        logger.info("Updating a task %s with id %s%n".formatted(task.getTitle(), task.getId()));
        server.send(new UpdateTaskMessage(task));
    }

    public void moveTask(UUID taskId, UUID sourceListId, UUID targetListId, BigFraction targetPosition) {
        logger.info("Moving a task %s from %s to %s at %s%n".formatted(taskId, sourceListId, targetListId, targetPosition));
        server.send(new MoveTaskMessage(taskId, sourceListId, targetListId, targetPosition));
    }

    public void removeTask(UUID taskId, UUID sourceListId) {
        logger.info("Removing a task %s from task list %s%n".formatted(taskId, sourceListId));
        server.send(new RemoveTaskMessage(taskId, sourceListId));
    }

    public void addTaskList(TaskList taskList) {
        logger.info("Adding a task list %s with id %s%n".formatted(taskList.getTitle(), taskList.getId()));
        server.send(new AddTaskListMessage(taskList));
    }

    public void updateTaskList(TaskList taskList) {
        logger.info("Updating a task list %s with id %s%n".formatted(taskList.getTitle(), taskList.getId()));
        server.send(new UpdateTaskListMessage(taskList));
    }

    public void removeTaskList(UUID taskListId) {
        logger.info("Removing a task list %s%n".formatted(taskListId));
        server.send(new RemoveTaskListMessage(taskListId));
    }

    public void updateBoard(Board board) {
        logger.info("Updating a board %s with id %s%n".formatted(board.getTitle(), board.getId()));
        server.send(new UpdateBoardMessage(board));
    }

    public void addTag(Tag tag) {
        logger.info("Creating a tag %s with id %s%n".formatted(tag.getName(), tag.getId()));
        server.send(new AddTagMessage(tag));
    }

    public void removeTag(UUID tagId) {
        logger.info("Removing a tag list %s%n".formatted(tagId));
        server.send(new RemoveTagMessage(tagId));
    }

    public void updateTag(Tag tag) {
        logger.info("Updating a tag %s with id %s%n".formatted(tag.getName(), tag.getId()));
        server.send(new UpdateTagMessage(tag));
    }

    public void removeBoard(UUID boardId) {
        logger.info("Removing a board %s%n".formatted(boardId));
        server.send(new RemoveBoardMessage(boardId));
    }

    public void createSubTask(UUID parentTaskId, SubTask createdSubTask) {
        logger.info("Creating a subtask %s with id %s%n".formatted(createdSubTask.getTitle(), createdSubTask.getId()));
        server.send(new CreateSubtaskMessage(parentTaskId, createdSubTask));
    }

    public void removeSubtask(UUID subtaskId) {
        logger.info("Removing a subtask with id %s%n".formatted(subtaskId));
        server.send(new RemoveSubtaskMessage(subtaskId));
    }

    public void updateSubTask(SubTask subTask) {
        logger.info("Updating a subtask %s with id %s%n".formatted(subTask.getTitle(), subTask.getId()));
        server.send(new UpdateSubtaskMessage(subTask));
    }

    public void join(String joinKey) {
        if (userContext.getUsername() == null) throw new IllegalStateException("User does not have a username");
        System.out.printf("[Board Context] Joining a board %s as %s%n", joinKey, userContext.getUsername());

        if (boardModel != null) quit();
        subscribeAll();

        server.send(new JoinC2SMessage(userContext.getUsername(), joinKey));
    }

    public void quit() {
        System.out.printf("[Board Context] Quitting the board%n");

        unsubscribeAll();
        boardModel = null;

        server.send(new QuitC2SMessage());
    }

    public void subscribeAll() {
        this.subscriptions = List.of(
                server.listen(AddTaskMessage.class, message -> {
                    logger.info("Task added by peer: %s%n".formatted(message.getTask().getTitle()));
                    var list = boardModel.getListById(message.getTask().getTaskListId());
                    list.peerAddedTask(message.getTask());
                }),

                server.listen(RemoveTaskMessage.class, message -> {
                    logger.info("Task removed by peer: %s%n".formatted(message.getTaskId()));
                    var list = boardModel.getListById(message.getSourceListId());
                    list.peerRemovedTask(list.getTaskById(message.getTaskId()));
                }),

                server.listen(MoveTaskMessage.class, message -> {
                    logger.info("Task moved by peer: %s%n".formatted(message));

                    boardModel.peerMovedTask(message);
                }),

                server.listen(UpdateTaskMessage.class, message -> {
                    logger.info("Task updated by peer: %s%n".formatted(message.getTask()));
                    var list = boardModel.getListById(message.getTask().getTaskListId());
                    list.peerUpdatedTask(message.getTask());
                }),

                server.listen(InitBoardStateS2CMessage.class, message -> {
                    logger.info("Board Initialization: %s%n".formatted(message.getBoard()));

                    boardModel = boardModelFactory.create(message.getBoard());
                    userContext.getKeychain().updateEntry(message.getBoard().getInfo());

                    onModelInitialized.accept(boardModel);
                }),

                server.listen(AddTaskListMessage.class, (AddTaskListMessage message) -> {
                    logger.info("Task list added by peer: %s%n".formatted(message.getTaskList().getTitle()));
                    boardModel.peerAddedTaskList(message.getTaskList());
                }),

                server.listen(RemoveTaskListMessage.class, (RemoveTaskListMessage message) -> {
                    logger.info("Task list removed by peer: %s%n".formatted(message.getTaskListId()));
                    boardModel.peerRemovedTaskList(message.getTaskListId());
                }),

                server.listen(UpdateTaskListMessage.class, (UpdateTaskListMessage message) -> {
                    logger.info("Task list updated by peer: %s%n".formatted(message.getTaskList()));
                    boardModel.peerUpdatedTaskList(message.getTaskList());
                }),

                server.listen(UpdateBoardMessage.class, message -> {
                    logger.info("Board updated by peer: %s%n".formatted(message.getBoard()));
                    boardModel.peerUpdatedBoard(message.getBoard());
                }),

                server.listen(AddTagMessage.class, message -> {
                    logger.info("Tag added by peer: %s%n".formatted(message.getTag().getName()));
                    boardModel.peerAddedTag(message.getTag());
                }),

                server.listen(RemoveTagMessage.class, message -> {
                    logger.info("Tag removed by peer: %s%n".formatted(message.getRemovedTagId()));
                    boardModel.onDeleteTag(message.getRemovedTagId());
                }),

                server.listen(UpdateTagMessage.class, message -> {
                    logger.info("Tag updated by peer: %s%n".formatted(message.getTag()));
                    boardModel.peerUpdatedTag(message.getTag());
                }),

                server.listen(AddTagToTaskMessage.class, message -> {
                    logger.info("Tag added to task by peer: %s%n".formatted(message));
                    boardModel.peerAddedTagToTask(message.getTaskId(), message.getTagId());
                }),

                server.listen(RemoveTagFromTaskMessage.class, message -> {
                    logger.info("Tag removed from task by peer: %s%n".formatted(message));
                    boardModel.peerRemovedTagFromTask(message.getTaskId(), message.getTagId());
                }),

                server.listen(CreateSubtaskMessage.class, message -> {
                    logger.info("Subtask created by peer: %s%n".formatted(message.getCreatedSubtask().getTitle()));
                    boardModel.peerCreatedSubtask(message.getParentTaskId(), message.getCreatedSubtask());
                }),

                server.listen(UpdateSubtaskMessage.class, message -> {
                    logger.info("Subtask updated by peer: %s%n".formatted(message.getSubtask().getTitle()));
                    boardModel.peerUpdatedSubtask(message.getSubtask());
                }),

                server.listen(SwapSubtaskPositionMessage.class, message -> {
                    logger.info("Subtask position swapped by peer: %s%n".formatted(message));
                    boardModel.peerSwappedSubtaskPosition(message.getSubtaskId1(), message.getSubtaskId2());
                }),

                server.listen(RemoveSubtaskMessage.class, message -> {
                    logger.info("Subtask removed by peer: %s%n".formatted(message.getSubtaskId()));
                    boardModel.peerRemovedSubtask(message.getSubtaskId());
                }),

                server.listen(CreateHighlightMessage.class, message -> {
                    logger.info("Highlight created by peer: %s%n".formatted(message.getHighlight().getName()));
                    boardModel.peerCreatedHighlight(message.getHighlight());
                }),

                server.listen(UpdateHighlightMessage.class, message -> {
                    logger.info("Highlight updated by peer: %s%n".formatted(message.getHighlight().getName()));
                    boardModel.peerUpdatedHighlight(message.getHighlight());
                }),

                server.listen(RemoveHighlightMessage.class, message -> {
                    logger.info("Highlight removed by peer: %s%n".formatted(message.getRemovedHighlightId()));
                    boardModel.peerRemovedHighlight(message.getRemovedHighlightId());
                })
        );
    }

    private void unsubscribeAll() {
        for (var subscription : subscriptions) {
            subscription.unsubscribe();
        }
    }

    public void setOnModelInitialized(Consumer<BoardModel> onModelInitialized) {
        this.onModelInitialized = onModelInitialized;
    }

    public void addTagToTask(UUID taskId, UUID tagId) {
        logger.info("Adding tag %s to task %s%n".formatted(tagId, taskId));
        server.send(new AddTagToTaskMessage(taskId, tagId));
    }

    public void removeTagFromTask(UUID taskId, UUID tagId) {
        logger.info("Removing tag %s from task %s%n".formatted(tagId, taskId));
        server.send(new RemoveTagFromTaskMessage(taskId, tagId));
    }

    public void swapSubtaskPosition(UUID subtaskId1, UUID subtaskId2) {
        logger.info("Swapping subtask positions: %s and %s%n".formatted(subtaskId1, subtaskId2));
        server.send(new SwapSubtaskPositionMessage(subtaskId1, subtaskId2));
    }

    public void removeHighlight(UUID removedHighlightId) {
        logger.info("Removing highlight: %s%n".formatted(removedHighlightId));
        server.send(new RemoveHighlightMessage(removedHighlightId));
    }

    public void saveHighlight(TaskHighlight highlight) {
        logger.info("Saving highlight: %s%n".formatted(highlight));
        server.send(new UpdateHighlightMessage(highlight));
    }

    public void createHighlight(TaskHighlight toServerHighlight) {
        logger.info("Creating highlight: %s%n".formatted(toServerHighlight));
        server.send(new CreateHighlightMessage(toServerHighlight));
    }
}
