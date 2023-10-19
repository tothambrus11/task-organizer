package server.models;

import commons.messages.Message;
import commons.messages.bidirectional.*;
import commons.messages.c2s.JoinC2SMessage;
import commons.messages.c2s.QuitC2SMessage;
import commons.messages.s2c.InitBoardStateS2CMessage;
import commons.messages.s2c.JoinS2CMessage;
import commons.messages.s2c.QuitS2CMessage;
import commons.models.Board;
import commons.models.Task;
import commons.models.User;
import server.SpringContext;
import server.database.*;
import server.services.RefreshService;
import server.services.SessionService;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BoardSession {

    private final SessionService sessionService;
    private final RefreshService refreshService;
    private final BoardRepository boardRepository;

    private final Map<String, User> users = new HashMap<>();

    private final Board board;
    private final TaskRepository taskRepository;
    private final HighlightRepository highlightRepository;
    private TagRepository tagRepository;
    private long nextUserId = 1;
    private TaskListRepository taskListRepository;
    private SubTaskRepository subTaskRepository;


    public BoardSession(Board board) {
        this.sessionService = SpringContext.getBean(SessionService.class);
        this.refreshService = SpringContext.getBean(RefreshService.class);
        this.boardRepository = SpringContext.getBean(BoardRepository.class);
        this.taskRepository = SpringContext.getBean(TaskRepository.class);
        this.tagRepository = SpringContext.getBean(TagRepository.class);
        this.taskListRepository = SpringContext.getBean(TaskListRepository.class);
        this.subTaskRepository = SpringContext.getBean(SubTaskRepository.class);
        this.highlightRepository = SpringContext.getBean(HighlightRepository.class);

        this.board = board;
    }

    public BoardSession(Board board, SessionService sessionService, RefreshService refreshService, BoardRepository boardRepository, TaskRepository taskRepository, TagRepository tagRepository, TaskListRepository taskListRepository, SubTaskRepository subTaskRepository, HighlightRepository highlightRepository) {
        this.board = board;
        this.sessionService = sessionService;
        this.refreshService = refreshService;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.taskListRepository = taskListRepository;
        this.subTaskRepository = subTaskRepository;
        this.highlightRepository = highlightRepository;
    }

    private long generateUserId() {
        return nextUserId++;
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    public Board getBoard() {
        return board;
    }

    public List<String> getUsers() {
        return users.keySet().stream().toList();
    }

    private void save() {
        boardRepository.save(board);
    }

    public void join(String sessionId, JoinC2SMessage message) {
        System.out.printf("[Board] %s (%s) joined the board %s%n", message.getUsername(), sessionId, board.getKey());

        var user = new User(generateUserId(), message.getUsername());
        users.put(sessionId, user);

        sendOne(sessionId, new InitBoardStateS2CMessage(board));
        sendOthers(sessionId, new JoinS2CMessage(user));
    }

    public void quit(String sessionId, QuitC2SMessage message) {
        var user = users.remove(sessionId);

        System.out.printf("[Board] %s (%s) quit the board %s%n", user.getName(), sessionId, board.getKey());

        sendOthers(sessionId, new QuitS2CMessage(user.getId()));
    }

    public void addTask(String sessionId, AddTaskMessage message) {
        var task = message.getTask();

        var taskList = board.getTaskListById(task.getTaskListId());
        if (taskList == null) {
            System.out.printf("[Board] Failed to add task \"%s\" (%s), task list (%s) doesn't exist%n",
                    task.getTitle(), task.getId(), task.getTaskListId());
            return;
        }

        taskList.getTasks().add(task);
        task.setTaskList(taskList);
        taskRepository.save(task);

        System.out.printf("[Board] Added task \"%s\" (%s) to task list \"%s\" (%s)%n",
                task.getTitle(), task.getId(), taskList.getTitle(), taskList.getId());
        sendOthers(sessionId, message);
    }

    @Transactional
    public void updateTask(String sessionId, UpdateTaskMessage message) {
        var update = message.getTask();

        var taskList = board.getTaskListById(update.getTaskListId());
        if (taskList == null) {
            System.out.printf("[Board] Failed to update task \"%s\" (%s), task list (%s) doesn't exist%n",
                    update.getTitle(), update.getId(), update.getTaskListId());
            return;
        }

        var task = taskList.getTaskById(update.getId());
        if (task == null) {
            System.out.printf("[Board] Failed to update task \"%s\" (%s), task list \"%s\" (%s) doesn't contain a task with that id%n",
                    update.getTitle(), update.getId(), taskList.getTitle(), taskList.getId());
            return;
        }

        task.update(update);
        taskRepository.save(task);

        System.out.printf("[Board] Updated task \"%s\" (%s) in task list \"%s\" (%s)%n",
                update.getTitle(), update.getId(), taskList.getTitle(), taskList.getId());
        sendOthers(sessionId, new UpdateTaskMessage(update));
    }

    public void moveTask(String sessionId, MoveTaskMessage message) {
        var sourceTaskList = board.getTaskListById(message.getSourceListId());
        if (sourceTaskList == null) {
            System.out.printf("[Board] Failed to move task, source task list (%s) doesn't exist%n", message.getSourceListId());
            return;
        }

        var targetTaskList = board.getTaskListById(message.getTargetListId());
        if (targetTaskList == null) {
            System.out.printf("[Board] Failed to move task, target task list (%s) doesn't exist%n", message.getTargetListId());
            return;
        }

        var task = sourceTaskList.getTaskById(message.getTaskId());
        if (task == null) {
            System.out.printf("[Board] Failed to move task (%s), source task list (%s) doesn't contain a task with that id%n",
                    message.getTaskId(), message.getSourceListId());
            return;
        }

        sourceTaskList.getTasks().remove(task);
        targetTaskList.getTasks().add(task);
        task.setTaskList(targetTaskList);
        task.setPosition(message.getTargetPosition());
        taskRepository.save(task);

        System.out.printf("[Board] Moved task \"%s\" (%s), from task list \"%s\" (%s) to \"%s\" (%s) at position %s%n",
                task.getTitle(), task.getId(), sourceTaskList.getTitle(), sourceTaskList.getId(), targetTaskList.getTitle(),
                targetTaskList.getId(), task.getPosition());
        sendOthers(sessionId, message);
    }

    public void removeTask(String sessionId, RemoveTaskMessage message) {
        var sourceTaskList = board.getTaskListById(message.getSourceListId());
        if (sourceTaskList == null) {
            System.out.printf("[Board] Failed to remove task, source task list (%s) doesn't exist%n", message.getSourceListId());
            return;
        }

        var task = sourceTaskList.getTaskById(message.getTaskId());
        if (task == null) {
            System.out.printf("[Board] Failed to remove task (%s), source task list (%s) doesn't contain a task with that id%n",
                    message.getTaskId(), message.getSourceListId());
            return;
        }

        sourceTaskList.getTasks().remove(task);
        save();

        System.out.printf("[Board] Removed task \"%s\" (%s), from task list \"%s\" (%s)%n",
                task.getTitle(), task.getId(), sourceTaskList.getTitle(), sourceTaskList.getId());
        sendOthers(sessionId, message);
    }

    public void addTaskList(String sessionId, AddTaskListMessage message) {
        var taskList = message.getTaskList();
        System.out.printf("[Board] Adding task list \"%s\" (%s)%n", taskList.getTitle(), taskList.getId());

        board.getTaskLists().add(taskList);
        taskList.setBoard(board);
        save();

        sendOthers(sessionId, message);
    }

    public void updateTaskList(String sessionId, UpdateTaskListMessage message) {
        var update = message.getTaskList();

        var taskList = board.getTaskListById(update.getId());
        if (taskList == null) {
            System.out.printf("[Board] Failed to update task list \"%s\" (%s), board \"%s\" (%s) doesn't contain a task list with that id%n",
                    update.getTitle(), update.getId(), board.getTitle(), board.getId());
            return;
        }

        taskList.update(update);
        taskListRepository.save(taskList);

        System.out.printf("[Board] Updated task list \"%s\" (%s) in board \"%s\" (%s)%n",
                update.getTitle(), update.getId(), board.getTitle(), board.getId());
        sendOthers(sessionId, message);
    }

    public void removeTaskList(String sessionId, RemoveTaskListMessage message) {
        var taskList = board.getTaskListById(message.getTaskListId());
        if (taskList == null) {
            System.out.printf("[Board] Failed to delete task list (%s), board \"%s\" (%s) doesn't contain a task list with that id%n",
                    message.getTaskListId(), board.getTitle(), board.getId());
            return;
        }


        taskList.getTasks().forEach(task -> {
            subTaskRepository.deleteAll(task.getSubTasks());
        });

        for (Task task : taskList.getTasks()) {
            task.setTaskList(null);
        }

        board.getTaskLists().remove(taskList);
        taskListRepository.delete(taskList);

        System.out.printf("[Board] Removed task list \"%s\" (%s), from board \"%s\" (%s)%n",
                taskList.getTitle(), taskList.getId(), board.getTitle(), board.getId());
        sendOthers(sessionId, message);
    }

    public void updateBoard(String sessionId, UpdateBoardMessage message) {
        var update = message.getBoard();

        board.update(update);
        save();

        refreshService.refresh();

        System.out.printf("[Board] Updated board \"%s\" (%s)%n", board.getTitle(), board.getId());
        sendOthers(sessionId, message);
    }

    public void removeBoard(String sessionId, RemoveBoardMessage message) {
        boardRepository.delete(board);

        refreshService.refresh();

        System.out.printf("[Board] Removed board \"%s\" (%s)%n", board.getTitle(), board.getId());
        sendOthers(sessionId, message);

        sessionService.closeSession(this);
    }

    /**
     * Send a message to all users of this board except "avoidSessionId".
     *
     * @param avoidSessionId - session id of the user to avoid
     * @param message        - the message to be sent
     */
    private void sendOthers(String avoidSessionId, Message message) {
        for (var user : users.keySet()) {
            if (avoidSessionId.equals(user)) continue;
            sessionService.send(user, message);
        }
    }

    public void updateTag(String sessionId, UpdateTagMessage message) {
        var update = message.getTag();

        var tag = board.getTagById(update.getId());
        if (tag.isEmpty()) {
            System.out.printf("[Board] Failed to update tag \"%s\" (%s), board \"%s\" (%s) doesn't contain a tag with that id%n",
                    update.getName(), update.getId(), board.getTitle(), board.getId());
            return;
        }

        tag.get().update(update);
        tagRepository.save(tag.get());

        System.out.printf("[Board] Updated tag \"%s\" (%s) in board \"%s\" (%s)%n",
                update.getName(), update.getId(), board.getTitle(), board.getId());
        sendOthers(sessionId, message);
    }

    public void addTag(String sessionId, AddTagMessage message) {
        System.out.printf("[Board] Adding tag \"%s\" (%s)%n", message.getTag().getName(), message.getTag().getId());
        var tag = message.getTag();
        tag.setBoard(board);
        board.getTags().add(tag);
        tagRepository.save(tag);
        sendOthers(sessionId, message);
    }

    public void removeTag(String sessionId, RemoveTagMessage message) {
        var tag = board.getTagById(message.getRemovedTagId());
        if (tag.isEmpty()) {
            System.out.printf("[Board] Failed to delete tag (%s), board \"%s\" (%s) doesn't contain a tag with that id%n",
                    message.getRemovedTagId(), board.getTitle(), board.getId());
            return;
        }

        // remove tag from all tasks
        for (var taskList : board.getTaskLists()) {
            for (var task : taskList.getTasks()) {
                System.out.println(task);
                task.getTags().remove(message.getRemovedTagId());
            }
        }
        boardRepository.save(board);

        // delete tag
        board.getTags().remove(tag.get());
        tagRepository.delete(tag.get());

        System.out.printf("[Board] Removed tag \"%s\" (%s), from board \"%s\" (%s)%n",
                tag.get().getName(), tag.get().getId(), board.getTitle(), board.getId());
        sendOthers(sessionId, message);
    }

    public void addTagToTask(String sessionId, AddTagToTaskMessage message) {
        var task = board.getTaskById(message.getTaskId());
        if (task.isEmpty()) {
            throw new NoSuchElementException("Task not found");
        }

        var tag = board.getTagById(message.getTagId());
        if (tag.isEmpty()) {
            throw new NoSuchElementException("Tag not found");
        }

        task.get().getTags().add(tag.get().getId());
        tag.get().getTasks().add(task.get().getId());
        save();

        sendOthers(sessionId, message);
    }

    public void removeTagFromTask(String sessionId, RemoveTagFromTaskMessage message) {
        var task = board.getTaskById(message.getTaskId());
        if (task.isEmpty()) {
            throw new NoSuchElementException("Task not found");
        }

        var tag = board.getTagById(message.getTagId());
        if (tag.isEmpty()) {
            throw new NoSuchElementException("Tag not found");
        }

        task.get().getTags().remove(tag.get().getId());
        tag.get().getTasks().remove(task.get().getId());
        save();

        sendOthers(sessionId, message);
    }

    public void createSubtask(String sessionId, CreateSubtaskMessage message) {
        var task = board.getTaskById(message.getParentTaskId());
        if (task.isEmpty()) {
            throw new NoSuchElementException("Task not found");
        }

        var subtask = message.getCreatedSubtask();
        subtask.setTask(task.get());
        task.get().getSubTasks().add(subtask);
        save();

        sendOthers(sessionId, message);
    }

    public void updateSubtask(String sessionId, UpdateSubtaskMessage message) {
        var subtask = board.getSubtaskById(message.getSubtask().getId());
        if (subtask.isEmpty()) {
            throw new NoSuchElementException("Subtask not found");
        }

        subtask.get().update(message.getSubtask());
        save();

        sendOthers(sessionId, message);
    }

    /**
     * Send a message to one user
     *
     * @param sessionId - the session id of the user
     * @param message   - the message to be sent
     */
    private void sendOne(String sessionId, Message message) {
        sessionService.send(sessionId, message);
    }

    public void swapSubtaskPosition(String sessionId, SwapSubtaskPositionMessage message) {
        var subtask1 = board.getSubtaskById(message.getSubtaskId1());
        if (subtask1.isEmpty()) {
            throw new NoSuchElementException("Subtask not found");
        }

        var subtask2 = board.getSubtaskById(message.getSubtaskId2());
        if (subtask2.isEmpty()) {
            throw new NoSuchElementException("Subtask not found");
        }

        var tempPos = subtask1.get().getPosition();
        subtask1.get().setPosition(subtask2.get().getPosition());
        subtask2.get().setPosition(tempPos);

        save();
        sendOthers(sessionId, message);
    }

    public void removeSubtask(String sessionId, RemoveSubtaskMessage message) {
        var subtask = board.getSubtaskById(message.getSubtaskId());
        if (subtask.isEmpty()) {
            throw new NoSuchElementException("Subtask not found");
        }

        var task = subtask.get().getTask();
        task.getSubTasks().remove(subtask.get());
        subTaskRepository.delete(subtask.get());

        save();
        sendOthers(sessionId, message);
    }

    public void createHighlight(String sessionId, CreateHighlightMessage message) {
        var highlight = message.getHighlight();
        highlight.setBoard(board);
        board.getHighlights().add(highlight);
        highlightRepository.save(highlight);
        sendOthers(sessionId, message);
    }

    public void updateHighlight(String sessionId, UpdateHighlightMessage message) {
        var highlight = board.getHighlightById(message.getHighlight().getId());
        if (highlight.isEmpty()) {
            throw new NoSuchElementException("Highlight not found");
        }
        highlight.get().update(message.getHighlight());
        highlightRepository.save(highlight.get());
        sendOthers(sessionId, message);
    }

    public void removeHighlight(String sessionId, RemoveHighlightMessage message) {
        var highlight = board.getHighlightById(message.getRemovedHighlightId());
        if (highlight.isEmpty()) {
            throw new NoSuchElementException("Highlight not found");
        }
        board.getHighlights().remove(highlight.get());
        highlightRepository.delete(highlight.get());
        save();
        sendOthers(sessionId, message);
    }
}
