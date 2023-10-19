package client.components;

import commons.utils.SmartColor;


public class ColorGenerator {

    public SmartColor calculateHoverForegroundColor(SmartColor foreground, SmartColor background) {
        return foreground
                .clone()
                .multiply(background.brightness() > 0.5 ? 0.6 : 1.1);
    }

    public SmartColor calculateHoverBackgroundColor(SmartColor background) {
        return background
                .clone()
                .multiply(background.brightness() > 0.5 ? 0.9 : 0.5);
    }

}
