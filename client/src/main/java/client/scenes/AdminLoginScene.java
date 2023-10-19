package client.scenes;

import client.App;
import client.components.KeyJoinText;
import client.components.TextInput;
import client.contexts.SessionContext;
import client.contexts.UserContext;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginScene implements Initializable {
    @FXML
    private TextInput serverAddress;
    @FXML
    private KeyJoinText password;
    @FXML
    private Button button;
    @FXML
    private Button login;

    @Inject
    private App app;

    @Inject
    private ServerUtils serverUtils;

    @Inject
    private UserContext userContext;

    @Inject
    private SessionContext sessionContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login.setOnAction(event->loginPress());
        button.setOnAction(event -> app.showUserLogin());
    }

    public void loginPress(){

        try {
            System.out.println(serverAddress.getText());
            serverUtils.connectToServer(serverAddress.getText());
            if (!userContext.loginAdmin(password.getText())) return;
            app.showWorkspace();
        } catch (IllegalArgumentException e) {
            // TODO: show error (server is not reachable)
        }
    }
}
