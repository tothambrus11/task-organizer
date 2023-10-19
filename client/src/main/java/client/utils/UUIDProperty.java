package client.utils;

import javafx.beans.property.SimpleObjectProperty;

import java.util.UUID;

public class UUIDProperty extends SimpleObjectProperty<UUID>{
    public UUIDProperty() {
        super();
    }

    public UUIDProperty(UUID initialValue) {
        super(initialValue);
    }

    public UUIDProperty(Object bean, String name) {
        super(bean, name);
    }

    public UUIDProperty(Object bean, String name, UUID initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    public void set(UUID newValue) {
        super.set(newValue);
    }

    public void set(String newValue) {
        super.set(UUID.fromString(newValue));
    }


    @Override
    public String toString() {
        return "UUIDProperty{"+ get() +"}";
    }
}
