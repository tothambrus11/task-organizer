package commons;

import commons.utils.SmartColor;

public class Constants {
    // ========================      ROUTING      ========================

    public static final String API_PATH = "/api";
    public static final String WEBSOCKET_PATH = "/websocket";

    // ========================   DEFAULT COLORS      ====================
    public static final SmartColor DEFAULT_BOARD_BACKGROUND_COLOR = SmartColor.valueOf("#efefef");
    public static final SmartColor DEFAULT_BOARD_FOREGROUND_COLOR = SmartColor.valueOf("#1D1D1D");
    public static final SmartColor DEFAULT_LIST_BACKGROUND_COLOR = SmartColor.valueOf("#E6E6E6");
    public static final SmartColor DEFAULT_LIST_FOREGROUND_COLOR = SmartColor.valueOf("#1D1D1D");
    public static final SmartColor DEFAULT_HIGHLIGHT_FOREGROUND_COLOR = SmartColor.valueOf("#1D1D1D");
    public static final SmartColor DEFAULT_HIGHLIGHT_BACKGROUND_COLOR = SmartColor.valueOf("white");

    // ========================       OTHER      ========================

    public static final int WORKSPACE_REFRESH_INTERVAL = 15; // seconds

}
