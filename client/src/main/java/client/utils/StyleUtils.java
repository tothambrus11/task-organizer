package client.utils;

import commons.utils.SmartColor;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class StyleUtils {
    public ObservableValue<SmartColor> backgroundOf(Pane node){
        return node.backgroundProperty()
                .map(background -> SmartColor.valueOf((Color) background.getFills().get(0).getFill()))
                .orElse(new SmartColor(1, 0, 0, 1));
    }
}
