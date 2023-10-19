package client;

import java.io.IOException;

public class SimpleFXMLLoader {
    /**
     * Initializes a controller with the given fxml file and injects all @FXML annotated fields.
     * For use in controllers that are injected by DI.
     * @param controller
     * @param path
     */
    public void initView(String path, Object controller){
        _initView(path, controller);
    }

    /**
     * For use in controllers that are not injected by DI but created by FXMLLoader (inside a fxml file).
     * Those can't have constructor injection, so we need to call this statically. Using this is not recommended, only
     * if you are writing a component that is not injected by DI and that you want to use in a fxml file.
     * @param path - path to the fxml file separated by /
     * @param controller - the controller that is created by FXMLLoader (usually "this")
     */
    public static void _initView(String path, Object controller){
        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(SimpleFXMLLoader.class.getResource(path));
        fxmlLoader.setRoot(controller);
        fxmlLoader.setController(controller);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
