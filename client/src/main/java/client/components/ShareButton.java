package client.components;

import client.models.BoardModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.scene.control.Button;

public class ShareButton extends Button {

    private final SimplePopup popup;
    private SimplePopup.Factory simplePopupFactory;
    @Inject
    public ShareButton(SimplePopup.Factory simplePopupFactory, @Assisted BoardModel boardModel){
        super("Share");
        this.getStyleClass().add("ShareButton");
        this.simplePopupFactory = simplePopupFactory;

        popup = this.simplePopupFactory.create();
        SharePopupContent sharePopupContent = new SharePopupContent(boardModel);

        popup.setFollowed(this);
        popup.setInnerContent(sharePopupContent);


        popup.setWidth(254);
        popup.setSpaceBetweenPopupAndFollowed(20);



        this.setOnAction(event -> {
            popup.showPopup();
        });
    }
    public interface Factory {
        ShareButton create(BoardModel boardModel);
    }
}
