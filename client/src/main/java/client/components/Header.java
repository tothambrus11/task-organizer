package client.components;

import client.App;
import client.models.BoardModel;
import client.contexts.SessionContext;
import client.views.CustomizationPopupContent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;


public class Header extends GridPane {

    private final App app;
    private final SessionContext sessionContext;

    private HeaderTitle titleInput;
    private BoardModel boardModel;
    private IconButton editTitleButton;
    private IconButton backToHomeButton;
    private HBox titleContainer;
    private ShareButton.Factory shareButtonFactory;
    private TagsButton.Factory tagButtonFactory;
    private SimplePopup.Factory simplePopupFactory;
    private CustomizationPopupContent.Factory customizationPopupFactory;

    @Inject
    private Header(@Assisted BoardModel boardModel,
                   App app,
                   ShareButton.Factory shareButtonFactory,
                   TagsButton.Factory tagButtonFactory,
                   SessionContext sessionContext,
                   SimplePopup.Factory simplePopupFactory,
                   CustomizationPopupContent.Factory customizationPopupFactory) {
        this.app = app;
        this.sessionContext = sessionContext;

        this.customizationPopupFactory = customizationPopupFactory;
        this.boardModel = boardModel;
        this.shareButtonFactory = shareButtonFactory;
        this.tagButtonFactory = tagButtonFactory;
        this.simplePopupFactory = simplePopupFactory;

        this.setHeight(62);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setStyle("-fx-background-color: white; -fx-padding: 0 13 0 13;");

        var leftHeader = initLeftHeader();
        var emptySpace = initEmptySpace();
        var rightHeader = initRightHeader();

        this.add(leftHeader, 0, 0);
        this.add(emptySpace, 1, 0);
        this.add(rightHeader, 2, 0);


        this.setMinSize(0, 0);
        this.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.prefWidthProperty().bind(newScene.widthProperty());
                titleInput.maxWidthProperty().bind(newScene.widthProperty().subtract(rightHeader.getWidth() + 300));
            }
        });
    }

    private Pane initEmptySpace() {
        Pane emptySpace = new Pane();
        GridPane.setHgrow(emptySpace, Priority.ALWAYS);
        return emptySpace;
    }

    private IconButton initSettingsButton() {
        IconButton settingsButton = new IconButton();
        settingsButton.setSize(IconButton.ButtonSize.LARGE);
        settingsButton.setSource("/client/icons/settings_large.png");
        settingsButton.foregroundColorProperty().bind(boardModel.boardForegroundColorProperty());

        var popup = simplePopupFactory.create();
        var content = customizationPopupFactory.create(boardModel);


        popup.setFollowed(settingsButton);
        popup.setInnerContent(content);
        popup.setSpaceBetweenPopupAndFollowed(20);

        settingsButton.setOnAction(event -> {
            popup.showPopup();
        });

        return settingsButton;
    }

    private HBox initRightHeader() {
        HBox container = new HBox();

        var settingsButton = initSettingsButton();
        var shareButton = shareButtonFactory.create(boardModel);
        var tagsButton = tagButtonFactory.create(boardModel);

        container.getChildren().addAll(tagsButton, settingsButton, shareButton);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setSpacing(10);
        GridPane.setHgrow(container, Priority.ALWAYS);

        return container;
    }

    private IconButton initBackToHomeButton() {
        var btn = new IconButton();
        btn.setSize(IconButton.ButtonSize.LARGE);
        btn.setSource("/client/icons/back_to_home_large.png");
        btn.foregroundColorProperty().bind(boardModel.boardForegroundColorProperty());
        btn.setOnAction(event -> {
            sessionContext.quit();
            app.showWorkspace();
        });

        return btn;
    }
//    private Button initTagsButton(){
//        var btn = new Button();
//        btn.setText("Tags");
//        btn.getStyleClass().add("TagsButton");
//        btn.setOnAction(event -> app.getBoardSceneCtrl().showTagsViewPopup());
//        return btn;
//    }

    private HBox initLeftHeader() {
        HBox container = new HBox();
        container.setPrefHeight(62);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);

        this.backToHomeButton = initBackToHomeButton();
        this.titleContainer = initTitleContainer();
        this.editTitleButton = initTitleButton(titleInput);
        container.getChildren().addAll(backToHomeButton, titleContainer, editTitleButton);

        return container;
    }

    private HBox initTitleContainer() {
        titleInput = initTitleInput();

        HBox headerTitleContainer = new HBox(titleInput);
        headerTitleContainer.setAlignment(Pos.CENTER);

        headerTitleContainer.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                titleInput.setEditable(true);
                titleInput.requestFocus();
                titleInput.setMouseTransparent(false);
            }
        });

        return headerTitleContainer;
    }

    private IconButton initTitleButton(HeaderTitle titleInput) {
        IconButton editTitle = new IconButton();
        editTitle.setSource("/client/icons/edit_small.png");
        editTitle.foregroundColorProperty().bind(boardModel.boardForegroundColorProperty());

        editTitle.setOnAction(event -> {
            titleInput.setEditable(true);
            titleInput.requestFocus();
            titleInput.setMouseTransparent(false);
        });
        return editTitle;
    }

    private HeaderTitle initTitleInput() {
        HeaderTitle titleInput = new HeaderTitle();
        titleInput.setMaxWidth(1000000);
        titleInput.setText(boardModel.getTitle());
        titleInput.styleProperty().bind(boardModel.boardForegroundColorProperty().asString("-fx-text-fill: %s;"));

        titleInput.setEditable(false);
        titleInput.setMouseTransparent(true);
        titleInput.setFocusTraversable(false);

        titleInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                titleInput.getParent().requestFocus();
            }
        });

        titleInput.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                titleInput.setEditable(false);
                titleInput.setMouseTransparent(true);
                if (titleInput.getText().isEmpty()) titleInput.setText(boardModel.getTitle());
                else {
                    boardModel.setTitle(titleInput.getText());
                    boardModel.save();
                }
            }
        }));

        boardModel.titleProperty().addListener((observable, oldValue, newValue) -> titleInput.setText(newValue));

        return titleInput;
    }

    public interface Factory {
        Header create(BoardModel boardModel);
    }
}
