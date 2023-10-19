package server.services;

import commons.Constants;
import commons.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.*;
import server.utils.RandomKeyGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator();
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TaskHighlightRepository taskHighlightRepository;
    @Autowired
    private TaskListRepository taskListRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SubTaskRepository subTaskRepository;
    @Autowired
    private RefreshService refreshService;

    public Optional<Board> findBoardWithAssociations(String key) {
        Optional<Board> optionalBoard = boardRepository.findByKey(key);
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();

            // fetch related highlights
            List<TaskHighlight> taskHighlights = taskHighlightRepository.findByBoardId(board.getId());
            board.setHighlights(taskHighlights);

            // fetch related tags for the board
            List<Tag> tags = tagRepository.findByBoardId(board.getId());
            board.setTags(tags);

            for (Tag tag : tags) {
                tag.setBoard(board);
            }

            // fetch related task lists, tasks, and subtasks for the board
            List<TaskList> taskLists = taskListRepository.findByBoardId(board.getId());
            for (TaskList taskList : taskLists) {
                taskList.setBoard(board);

                List<Task> tasks = taskRepository.findByTaskListId(taskList.getId());
                taskList.setTasks(tasks);

                for (Task task : tasks) {
                    task.setTaskList(taskList);

                    List<SubTask> subTasks = subTaskRepository.findByTaskId(task.getId());
                    task.setSubTasks(subTasks);

                    for (SubTask subTask : subTasks) {
                        subTask.setTask(task);
                    }
                }
            }
            board.setTaskLists(taskLists);

            // fetch related highlights
            List<TaskHighlight> highlights = taskHighlightRepository.findByBoardId(board.getId());
            board.setHighlights(highlights);
        }

        return optionalBoard;
    }

    public Board createBoard(String creator) {
        Board board = new Board();

        // Set the initial values for the board from the request
        board.setTitle("New Board");
        board.setCreator(creator);
        board.setShared(true);

        board.setBoardBackgroundColor(Constants.DEFAULT_BOARD_BACKGROUND_COLOR);
        board.setBoardForegroundColor(Constants.DEFAULT_BOARD_FOREGROUND_COLOR);
        board.setListBackgroundColor(Constants.DEFAULT_LIST_BACKGROUND_COLOR);
        board.setListForegroundColor(Constants.DEFAULT_LIST_FOREGROUND_COLOR);


        var highlight = new TaskHighlight(
                UUID.randomUUID(),
                "Default Highlight",
                Constants.DEFAULT_HIGHLIGHT_FOREGROUND_COLOR,
                Constants.DEFAULT_HIGHLIGHT_BACKGROUND_COLOR,
                1L
        );
        highlight.setBoard(board);
        //Create default highlight for the board
        board.getHighlights().add(highlight);
        board.setDefaultHighlightId(highlight.getId());

        // Generate a unique 6-character alphanumeric key for the board
        board.setKey(generateRandomUniqueKey());

        // Save the board using the BoardRepository
        Board createdBoard = boardRepository.save(board);

        refreshService.refresh();

        return createdBoard;
    }

    private String generateRandomUniqueKey() {
        String key;
        do {
            key = randomKeyGenerator.generateKey();
        } while (boardRepository.findByKey(key).isPresent());

        return key;
    }

}