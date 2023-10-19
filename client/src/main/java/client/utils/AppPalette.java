package client.utils;

import client.components.ColorGenerator;
import com.google.inject.Inject;
import commons.utils.SmartColor;

public class AppPalette {

    private final SmartColor defaultIconForeground = new SmartColor(0.5725490196, 0.51372549019, 0.51372549019, 1);
    private final SmartColor blackIconForeground = new SmartColor(0, 0, 0, 1);
    private final ColorGenerator colorGenerator;
    @Inject
    public AppPalette(ColorGenerator colorGenerator) {
        this.colorGenerator = colorGenerator;
    }

    public SmartColor getBlackIconForeground() {
        return blackIconForeground;
    }

    public SmartColor getDefaultIconForeground() {
        return defaultIconForeground;
    }

    public ColorGenerator getColorGenerator() {
        return colorGenerator;
    }
}
