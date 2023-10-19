package client.components;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class NonRecyclingListView<T extends NonRecyclingListView.CanBeDummy> extends Pane {
    private Pane container;

    public NonRecyclingListView(Orientation orientation, ObservableList<T> items, ListCellFactory<T> cellFactory) {
        super();
        if (orientation == Orientation.HORIZONTAL) {
            container = new HBox();
        } else {
            container = new VBox();
        }
        getChildren().add(container);


        // add elements initially
        for (T item : items) {
            if (!item.isDummy()) {
                container.getChildren().add(cellFactory.createCell(item));
            }
        }

        // keep the list up to date
        items.addListener((ListChangeListener<T>) c -> {
            System.out.println("List changed" + items.size());
            while (c.next()) {
                if (c.wasAdded()) {
                    for (T item : c.getAddedSubList()) {
                        if (!item.isDummy()) {
                            container.getChildren().add(cellFactory.createCell(item));
                        }
                    }
                }
                if (c.wasRemoved()) {
                    for (T item : c.getRemoved()) {
                        container.getChildren().removeIf(node -> ((ListCell<T>) node).getModelItem().equals(item));
                    }
                }
            }
        });
    }

    public Pane getContainer() {
        return container;
    }


    public interface ListCellFactory<T extends CanBeDummy> {
        <U extends Node & ListCell<T>> U createCell(T item);
    }

    public interface ListCell<T extends CanBeDummy> {
        T getModelItem();
        void onRemoved();
    }

    public interface CanBeDummy {
        boolean isDummy();
    }
}
