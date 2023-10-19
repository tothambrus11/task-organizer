package client.components;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class HeaderTitle extends TextField {

    public HeaderTitle(){
        super();
        this.getStyleClass().add("Title");

        setMinWidth(50);
        setMaxWidth(Region.USE_PREF_SIZE);

        textProperty().addListener((obs, oldValue, newValue) -> Platform.runLater(this::autoResize));
        new Thread(()->{
            try {
                // wait for actual font to be loaded
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(200);
                    Platform.runLater(this::autoResize);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private void autoResize(){
        Text text = new Text(getText());
        text.setFont(this.getFont());
        double width = text.getLayoutBounds().getWidth() + 2d;
        this.setPrefWidth(width);
    }
}
