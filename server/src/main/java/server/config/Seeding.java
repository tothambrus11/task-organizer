package server.config;

import com.github.kiprobinson.bigfraction.BigFraction;
import commons.models.*;
import commons.utils.SmartColor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.BoardRepository;
import server.database.SubTaskRepository;
import server.database.TagRepository;
import server.database.TaskListRepository;

import java.util.UUID;

@Configuration
public class Seeding {

    @Bean
    CommandLineRunner runner(BoardRepository boardRepository, TaskListRepository taskListRepository, SubTaskRepository subTaskRepository, TagRepository tagRepository) {
        return args -> {
            if (boardRepository.count() != 0) return;

            // Create Board
            Board board = new Board();
            board.setId(UUID.randomUUID());
            board.setTitle("Board 1");
            board.setCreator("Creator 1");
            board.setKey("ABC123");
            board.setShared(true);
            {
                // Create Board Colors
                board.setBoardBackgroundColor(new SmartColor(220, 149, 150, 1).div(255));
                board.setBoardForegroundColor(new SmartColor(0, 0, 0, 1).div(255));
                board.setListBackgroundColor(new SmartColor(120, 149, 130, 1).div(255));
                board.setListForegroundColor(new SmartColor(120, 119, 101, 1).div(255));

                // Create Custom Task Colors
                TaskHighlight color1 = new TaskHighlight();
                color1.setId(UUID.randomUUID());
                color1.setBackgroundColor(new SmartColor(57, 0, 64, 1));
                color1.setForegroundColor(new SmartColor(169, 165, 135, 1));
                color1.setPosition(1L);
                color1.setName("Color 1");

                // Create Tags
                Tag tag1 = new Tag(UUID.randomUUID(), "Example Tag 1", new SmartColor(1, 0.5, 0.5, 1));
                Tag tag2 = new Tag(UUID.randomUUID(), "Example Tag 2", new SmartColor(0, 0.5, 0.5, 1));
                Tag tag3 = new Tag(UUID.randomUUID(), "Example Tag 3", new SmartColor(1, 0.5, 0.5, 1));
                Tag tag4 = new Tag(UUID.randomUUID(), "Example Tag 4", new SmartColor(0, 0.5, 0.5, 1));
                Tag tag5 = new Tag(UUID.randomUUID(), "Example Tag 5", new SmartColor(0.5, 0.5, 0, 1));

                // Associate tags with board
                tag1.setBoard(board);
                tag2.setBoard(board);
                tag3.setBoard(board);
                tag4.setBoard(board);
                tag5.setBoard(board);

                // Add tags to the board's tags list
                board.getTags().add(tag1);
                board.getTags().add(tag2);
                board.getTags().add(tag3);
                board.getTags().add(tag4);
                board.getTags().add(tag5);


                // Set relationships
                color1.setBoard(board);
                board.getHighlights().add(color1);

                {
                    // Create Deck
                    TaskList taskList = new TaskList();
                    taskList.setId(UUID.randomUUID());
                    taskList.setTitle("Deck 1");
                    taskList.setPosition(new BigFraction(1, 3));

                    {
                        // Create Card
                        Task task = new Task(UUID.randomUUID(), "Card 1", "Description 1", new BigFraction(1, 3));

                        // Set relationships
                        task.setTaskList(taskList);
                        taskList.getTasks().add(task);

                        task.setTaskHighlightId(color1.getId());
                    }
                    {
                        // Create Card
                        Task task = new Task();
                        task.setId(UUID.randomUUID());
                        task.setTitle("Card 2");
                        task.setDescription("Description 2");
                        task.setPosition(new BigFraction(2, 3));


                        // Set relationships
                        task.setTaskList(taskList);
                        taskList.getTasks().add(task);

                        task.setTaskHighlightId(color1.getId());
                    }

                    taskList.setBoard(board);
                    board.getTaskLists().add(taskList);
                }
                {
                    // Create Deck
                    TaskList taskList = new TaskList();
                    taskList.setId(UUID.randomUUID());
                    taskList.setTitle("Deck 2");
                    taskList.setPosition(new BigFraction(2, 3));

                    {
                        // Create Card
                        Task task = new Task();
                        task.setId(UUID.randomUUID());
                        task.setTitle("Card 21");
                        task.setDescription("Description 21");
                        task.setPosition(new BigFraction(1, 3));

                        // Set relationships
                        task.setTaskList(taskList);
                        taskList.getTasks().add(task);

                        task.getTags().add(tag1.getId());
                        task.getTags().add(tag2.getId());
                        task.getTags().add(tag3.getId());
                        task.getTags().add(tag4.getId());
                        task.getTags().add(tag5.getId());

                        task.setTaskHighlightId(color1.getId());
                    }
                    {
                        // Create Card
                        Task task = new Task();
                        task.setId(UUID.randomUUID());
                        task.setTitle("Card 22");
                        task.setDescription("Description 22");
                        task.setPosition(new BigFraction(2, 3));

                        // Set relationships
                        task.setTaskList(taskList);
                        taskList.getTasks().add(task);


                        // add tags
                        task.getTags().add(tag4.getId());
                        task.getTags().add(tag5.getId());

                        task.setTaskHighlightId(color1.getId());
                    }

                    taskList.setBoard(board);
                    board.getTaskLists().add(taskList);
                }

            }
            // Save entities
            boardRepository.save(board);
        };
    }
}
