package client.di;

import client.components.BoardPreview;
import client.components.WorkspaceSharePopupContent;
import client.views.BoardPreviewCell;
import client.views.WorkspaceView;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import java.util.List;

public class WorkspaceModule extends AbstractModule {

    @Override
    protected void configure() {
        var factoriesToBuild = List.of(
                BoardPreview.Factory.class,
                BoardPreviewCell.Factory.class,
                WorkspaceView.Factory.class,
                WorkspaceSharePopupContent.Factory.class
        );

        factoriesToBuild.forEach(factory -> install(new FactoryModuleBuilder().build(factory)));
    }
}
