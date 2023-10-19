package client.scenes;

import client.components.MySwitch;
import client.components.RoundColorPicker;
import client.models.BoardModel;
import client.models.TagModel;
import client.views.TagsView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloWorldScene implements Initializable {

    @FXML
    private HBox container;
    @FXML
    private RoundColorPicker roundColorPicker1;

    @FXML
    private RoundColorPicker roundColorPicker2;

    @Inject
    private TagsView.Factory tagsViewFactory;

    @FXML
    private MySwitch showTasksSwitch;
    @Inject
    private BoardModel.Factory boardModelFactory;
    @Inject
    private TagModel.Factory tagModelFactory;


    @FXML
    private ComboBox<String> comboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        container.visibleProperty().bind(showTasksSwitch.stateProperty());
        roundColorPicker1.colorProperty().bind(roundColorPicker2.colorProperty());
    }
}
