package client.components;

import client.utils.FXTest;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SimplePopupTest extends FXTest {
    @Test
    public void fieldShouldBeNullWhenInstantiated(){
        SimplePopup popup = new SimplePopup();
        assertNull(popup.getFollowed());
        assertNull(popup.getInnerContent());
    }

    @Test
    public void defaultSpaceBetweenFieldsIs13(){
        SimplePopup popup = new SimplePopup();
        assertEquals(13, popup.getSpaceBetweenPopupAndFollowed());
    }

    @Test
    public void innerContentSetterUpdatesContent(){
        SimplePopup popup = new SimplePopup();
        HBox oldNode = new HBox();
        HBox newNode = new HBox();

        popup.setInnerContent(oldNode);
        popup.setInnerContent(newNode);

        assertEquals(newNode, popup.getInnerContent());
    }

    @Test
    public void popupIsInitialized(){
        SimplePopup popup = new SimplePopup();
        HBox followed = new HBox();
        HBox content = new HBox();

        popup.setInnerContent(content);
        popup.setFollowed(followed);

        assertTrue(popup.getIsInitialized());
    }
}
