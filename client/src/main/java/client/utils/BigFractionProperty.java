package client.utils;

import com.github.kiprobinson.bigfraction.BigFraction;
import javafx.beans.property.SimpleObjectProperty;

public class BigFractionProperty extends SimpleObjectProperty<BigFraction> {

    public BigFractionProperty() {
        super();
    }

    public BigFractionProperty(BigFraction initialValue) {
        super(initialValue);
    }

    public BigFractionProperty(Object bean, String name) {
        super(bean, name);
    }

    public BigFractionProperty(Object bean, String name, BigFraction initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    public void set(BigFraction newValue) {
        super.set(newValue);
    }

    public void set(String newValue) {
        super.set(BigFraction.valueOf(newValue));
    }

    @Override
    public String toString() {
        return "BigFractionProperty{"+ get() +"}";
    }

}
