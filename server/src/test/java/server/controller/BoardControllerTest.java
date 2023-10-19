package server.controller;

import commons.models.Board;
import commons.models.BoardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.controllers.BoardController;
import server.database.BoardRepository;
import server.services.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @InjectMocks
    private BoardController boardController;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardService boardService;

    private List<Board> boards;

    @BeforeEach
    public void setUp() {
        boards = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Board board = new Board();
            board.setId(UUID.randomUUID());
            board.setTitle("Board " + i);
            board.setCreator("Creator " + i);
            boards.add(board);
        }
    }

    @Test
    public void getAllTest() {
        when(boardRepository.findAll()).thenReturn(boards);

        ResponseEntity<List<BoardInfo>> response = boardController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(boards.size(), response.getBody().size());
        verify(boardRepository, times(1)).findAll();
    }

    @Test
    public void createBoardTest_success() {
        String creator = "Test Creator";
        Board newBoard = new Board();
        newBoard.setId(UUID.randomUUID());
        newBoard.setTitle("New Board");
        newBoard.setCreator(creator);

        when(boardService.createBoard(creator)).thenReturn(newBoard);

        ResponseEntity<BoardInfo> response = boardController.createBoard(creator);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newBoard.getInfo().getCreator(), response.getBody().getCreator());
        assertEquals(newBoard.getInfo().getId(), response.getBody().getId());
        assertEquals(newBoard.getInfo().getTitle(), response.getBody().getTitle());
        assertEquals(newBoard.getInfo().getJoinKey(), response.getBody().getJoinKey());
        assertEquals(newBoard.getInfo().getPassword(), response.getBody().getPassword());
        assertEquals(newBoard.getInfo().getLastJoinTime(), response.getBody().getLastJoinTime());

        verify(boardService, times(1)).createBoard(creator);
    }

    @Test
    public void createBoardTest_failure() {
        String creator = "Test Creator";
        when(boardService.createBoard(creator)).thenReturn(null);

        ResponseEntity<BoardInfo> response = boardController.createBoard(creator);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(boardService, times(1)).createBoard(creator);
    }
}