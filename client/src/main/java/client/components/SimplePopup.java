package client.components;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

public class SimplePopup extends Popup {
    private static final double POPUP_SHADOW_OFFSET_X = 30;
    private static final double POPUP_SHADOW_OFFSET_Y = 20;
    public final BooleanProperty isPopupShown = new SimpleBooleanProperty(false);
    private final BooleanProperty firstTimeShown = new SimpleBooleanProperty(true);
    long openedAt = 0;
    private ObjectProperty<Node> innerContent = new SimpleObjectProperty<>();
    private ObjectProperty<Node> followed = new SimpleObjectProperty<>();
    private DoubleProperty spaceBetweenPopupAndFollowed = new SimpleDoubleProperty(13);
    private BooleanBinding isInitialized = Bindings.createBooleanBinding(() -> getFollowed() != null && getInnerContent() != null, followed, innerContent);
    @Inject
    public SimplePopup() {
        isInitialized.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                initialize();
            }
        });
    }

    public Node getInnerContent() {
        return innerContent.get();
    }

    public void setInnerContent(Node innerContent) {
        this.innerContent.set(innerContent);
        this.getContent().removeAll();
        this.getContent().add(innerContent);
    }

    public ObjectProperty<Node> innerContentProperty() {
        return innerContent;
    }

    public Node getFollowed() {
        return followed.get();
    }

    public void setFollowed(Node followed) {
        this.followed.set(followed);
    }

    public ObjectProperty<Node> followedProperty() {
        return followed;
    }

    public double getSpaceBetweenPopupAndFollowed() {
        return spaceBetweenPopupAndFollowed.get();
    }

    public void setSpaceBetweenPopupAndFollowed(double spaceBetweenPopupAndFollowed) {
        this.spaceBetweenPopupAndFollowed.set(spaceBetweenPopupAndFollowed);
    }

    private void initialize() {
        if (!getInnerContent().getStyleClass().contains("popup-content")) {
            getInnerContent().getStyleClass().add("popup-content");
        }
        this.sizeToScene();

        this.setOnHidden(event -> isPopupShown.setValue(false));
        this.setOnShown(event -> isPopupShown.setValue(true));

        getFollowed().sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null || newScene.getWindow() == null) {
                return;
            }

            newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!isShowing()) return;
                event.consume();
                hide();
            });

            getFollowed().getScene().getWindow().xProperty().addListener((obs, oldValue, newValue) -> {
                setX(calculateXPos());
            });

            getFollowed().getScene().getWindow().yProperty().addListener((obs, oldValue, newValue) -> {
                setY(calculateYPos());
            });


            getFollowed().getScene().getWindow().showingProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue) this.hide();
            });

            newScene.getWindow().focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue) {
                    this.hide();
                }
            });


            // we need to show the popup once to get its size
            this.show(getFollowed(), 0.0, 0.0);
            this.hide();
        });

    }

    public void showPopup() {
        double x = calculateXPos();
        double y = calculateYPos();
        this.show(getFollowed(), x, y);

        openedAt = System.currentTimeMillis();
    }

    private double calculateYPos() {
        if(getFollowed().getScene() == null || getFollowed().getScene().getWindow() == null) return 0.0;
        var followedBottomY = getFollowed().localToScene(getFollowed().getBoundsInLocal()).getMaxY();
        var scenePosY = getFollowed().getScene().getY();
        var windowPosY = getFollowed().getScene().getWindow().getY();
        return followedBottomY + scenePosY + windowPosY + spaceBetweenPopupAndFollowed.get() - POPUP_SHADOW_OFFSET_Y;
    }

    private double calculateXPos() {
        if(getFollowed().getScene() == null || getFollowed().getScene().getWindow() == null) return 0.0;
        var followedLeft = getFollowed().localToScene(getFollowed().getBoundsInLocal()).getMinX();
        var scenePosX = getFollowed().getScene().getX();
        var followedWidth = getFollowed().getBoundsInLocal().getWidth();
        var innerContentWidth = getInnerContent().getLayoutBounds().getWidth();
        var windowX = getFollowed().getScene().getWindow().getX();

        return followedLeft + scenePosX + windowX - innerContentWidth + followedWidth - POPUP_SHADOW_OFFSET_X;
    }

    public Boolean getIsInitialized() {
        return isInitialized.get();
    }

    public BooleanBinding isInitializedProperty() {
        return isInitialized;
    }

    public interface Factory {
        SimplePopup create();
    }
}


