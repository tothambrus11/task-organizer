package server.services;

import commons.models.Board;
import commons.models.BoardInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.BoardRepository;

@Service
public class RefreshService {

    @Autowired
    private BoardRepository boardRepository;

    private List<DeferredResult<List<BoardInfo>>> deferredRefreshResults = new ArrayList<>();

    public void requireDeferredRefresh(DeferredResult<List<BoardInfo>> deferredResult) {
        deferredRefreshResults.add(deferredResult);
    }

    public void refresh() {
        for (var request : deferredRefreshResults) {
            var boards = boardRepository.findAll().stream().map(Board::getInfo).toList();
            request.setResult(boards);
        }
        deferredRefreshResults.clear();
    }
}
