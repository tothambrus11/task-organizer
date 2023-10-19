package server.controllers;

import commons.models.Board;
import commons.models.BoardInfo;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.BoardRepository;
import server.services.BoardService;
import server.services.RefreshService;

@RestController
@RequestMapping("/api/boards")

public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final RefreshService refreshService;

    @Autowired
    public BoardController(BoardRepository boardRepository, BoardService boardService, RefreshService refreshService) {
        this.boardRepository = boardRepository;
        this.boardService = boardService;
        this.refreshService = refreshService;
    }

    @GetMapping(path = { "", "/" })
    public ResponseEntity<List<BoardInfo>> getAll() {
        var boards = boardRepository.findAll().stream().map(Board::getInfo).toList();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("{boardId}")
    public ResponseEntity<BoardInfo> getBoardById(@PathVariable String boardId) {
        System.out.println("board queried with id " + boardId);
        UUID uuid = UUID.fromString(boardId);
        var board = boardRepository.findById(uuid).orElse(null);
        if (board != null) {
            return new ResponseEntity<>(board.getInfo(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<BoardInfo> createBoard(@RequestBody String creator) {
        Board createdBoard = boardService.createBoard(creator);
        if (createdBoard != null) {
            return new ResponseEntity<>(createdBoard.getInfo(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> deleteBoardById(@PathVariable String boardId) {
        System.out.println("[CONTROLLER] board deleted with id " + boardId);

        UUID uuid = UUID.fromString(boardId);
        var board = boardRepository.findById(uuid);
        if (board.isPresent()) {
            boardRepository.deleteById(uuid);
            refreshService.refresh();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{joinKey}/exists")
    public ResponseEntity<Boolean> getBoardExists(@PathVariable String joinKey) {
        System.out.println("[CONTROLLER] board queried with join key " + joinKey);
        var board = boardRepository.findByKey(joinKey);
        if (board.isPresent()) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    @RequestMapping("/refresh")
    @ResponseBody
    public DeferredResult<List<BoardInfo>> refreshAsync() {
        var deferredResult = new DeferredResult<List<BoardInfo>>();

        refreshService.requireDeferredRefresh(deferredResult);

        return deferredResult;
    }
}
