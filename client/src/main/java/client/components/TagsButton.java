package client.components;

import client.models.BoardModel;
import client.views.TagsView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.scene.control.Button;

public class TagsButton extends Button {
    private final SimplePopup popup;
    private SimplePopup.Factory simplePopupFactory;
    private TagsView tagsView;
    private TagsView.Factory tagsViewFactory;
    private final BoardModel boardModel;
    @Inject
    public TagsButton(@Assisted BoardModel boardModel, SimplePopup.Factory simplePopupFactory, TagsView.Factory tagsViewFactory){
        super("Tags");
        this.getStyleClass().add("TagsButton");
        this.simplePopupFactory = simplePopupFactory;
        this.tagsViewFactory = tagsViewFactory;
        this.boardModel = boardModel;

        popup = this.simplePopupFactory.create();
        tagsView = this.tagsViewFactory.create(boardModel);

        popup.setFollowed(this);
        popup.setInnerContent(tagsView);

        popup.setSpaceBetweenPopupAndFollowed(24);


        this.setOnAction(event -> {
            popup.showPopup();
        });
    }
    public interface Factory {
        TagsButton create(BoardModel boardModel);
    }
}
