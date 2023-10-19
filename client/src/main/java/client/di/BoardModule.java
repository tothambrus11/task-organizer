package client.di;

import client.components.*;
import client.models.*;
import client.views.*;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import java.util.List;

public class BoardModule extends AbstractModule {
    @Override
    protected void configure() {
        var factoriesToBuild = List.of(
                // Model factories
                BoardModel.Factory.class,
                TaskListModel.Factory.class,
                TaskListModel.DummyTaskListModel.Factory.class,
                TaskModel.DummyTask.Factory.class,
                TaskModel.Factory.class,
                TagModel.Factory.class,
                SubTaskModel.Factory.class,

                // View factories
                TaskCellContent.Factory.class,
                TaskListCell.Factory.class,
                BoardView.Factory.class,
                TaskListCellContent.Factory.class,
                TaskCell.Factory.class,
                TaskListView.Factory.class,
                TaskListView.Factory.class,
                SubTaskCell.Factory.class,
                SubTaskCellContent.Factory.class,
                SubTaskList.Factory.class,
                CustomizationPopupContent.Factory.class,
                HighlightListView.Factory.class,
                HighlightCell.Factory.class,
                HighlightCellContent.Factory.class,

                // Component factories
                ShareButton.Factory.class,
                SimplePopup.Factory.class,
                TaskDetailsPopup.Factory.class,
                Header.Factory.class,
                TagsButton.Factory.class,
                AddButton.Factory.class,

                //Tag factories
                TagsView.Factory.class,
                TagCell.Factory.class,
                TagCellContent.Factory.class,
                TagListView.Factory.class,
                TagModel.Factory.class,
                TagView.Factory.class,
                TagsOfTask.Factory.class,

                // Views within a card
                TagHighlightOnCard.Factory.class,
                TagHighlightListOnCard.Factory.class,
                TaskProgressView.Factory.class
        );

        for (var factory : factoriesToBuild) {
            install(new FactoryModuleBuilder().build(factory));
        }

    }
}

