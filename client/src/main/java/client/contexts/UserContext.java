package client.contexts;

import client.utils.Keychain;
import client.utils.ServerUtils;
import commons.models.BoardInfo;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private static final Logger logger = Logger.getLogger("UserContext");

    private final ServerUtils server;
    private Keychain keychain;

    private String username;

    private boolean isAdmin = false;
    private boolean isLoggedIn = false;

    List<Runnable> loginListeners = new ArrayList<>();

    public void addLoginListener(Runnable listener) {
        loginListeners.add(listener);
    }

    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    public UserContext(ServerUtils server) {
        this.server = server;

        keychain = new Keychain(server, this);
    }

    public void login(String name) {
        this.username = name;
        this.isAdmin = false;
        this.isLoggedIn = true;

        for (var listener : loginListeners) {
            listener.run();
        }
    }

    public boolean loginAdmin(String password) {
        var success = server.post("admin/auth", password, new GenericType<Boolean>() {});
        if (!success) {
            System.out.println("Incorrect password");
            return false;
        }

        this.isAdmin = true;
        this.isLoggedIn = true;

        for (var listener : loginListeners) {
            listener.run();
        }

        return true;
    }

    public void reset() {
        server.get("admin/reset", new GenericType<>() {});
    }

    public String getUsername() {
        return username;
    }

    public Keychain getKeychain() { return keychain; }
    public List<BoardInfo> retrieveAllBoards(){
        return server.get("boards", new GenericType<List<BoardInfo>>() {});
    }

    public BoardInfo createBoard() {
        logger.info("Creating a new board");
        return server.post("boards", username, new GenericType<>() {});
    }

    public void deleteBoard(UUID boardId) {
        logger.info("Deleting board " + boardId);
        server.delete("boards/" + boardId, new GenericType<>() {});
    }

    public BoardInfo getBoard(UUID boardId) {
        logger.info("Getting board info" + boardId);
        return server.get("boards/" + boardId, new GenericType<BoardInfo>() {});
    }

    public boolean getBoardExists(String joinKey) {
        logger.info("Checking if board exists: " + joinKey);
        return server.get("boards/" + joinKey + "/exists", new GenericType<Boolean>() {});
    }

    public void requestRefresh(Consumer<List<BoardInfo>> callback) {
        logger.info("Requesting refresh for all boards");
        var res = server.getAsync("boards/refresh", new GenericType<List<BoardInfo>>() {});
        res.thenAccept(callback);
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }
}
