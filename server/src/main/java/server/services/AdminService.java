package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.*;
import server.utils.AdminPasswordGenerator;

@Service
public class AdminService {

    @Autowired
    private RefreshService refreshService;

    @Autowired
    private AdminPasswordGenerator adminPasswordGenerator;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private SubTaskRepository subTaskRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TaskHighlightRepository taskHighlightRepository;
    @Autowired
    private TaskListRepository taskListRepository;
    @Autowired
    private TaskRepository taskRepository;

    public boolean auth(String password) {
        return adminPasswordGenerator.getAdminPassword().equals(password);
    }

    public void reset() {
        boardRepository.deleteAll();
        subTaskRepository.deleteAll();
        tagRepository.deleteAll();
        taskHighlightRepository.deleteAll();
        taskListRepository.deleteAll();
        taskRepository.deleteAll();

        refreshService.refresh();
    }
}
