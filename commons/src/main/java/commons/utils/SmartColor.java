package commons.utils;

import javafx.scene.paint.Color;

public class SmartColor {
    private double r;
    private double g;
    private double b;
    private double a;

    /**
     * Creates a {@code Color} with the specified red, green, blue, and alpha values in the range 0.0-1.0.
     *
     * @param red     red component ranging from {@code 0} to {@code 1}
     * @param green   green component ranging from {@code 0} to {@code 1}
     * @param blue    blue component ranging from {@code 0} to {@code 1}
     * @param opacity opacity ranging from {@code 0} to {@code 1}
     */
    public SmartColor(double red, double green, double blue, double opacity) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = opacity;
    }

    public static SmartColor valueOf(String c) {
        try {
            return valueOf(Color.valueOf(c));
        } catch (Exception e) {
            return null;
        }
    }

    public static SmartColor valueOf(Color c) {
        return new SmartColor(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
    }

    @Override
    public String toString() {
        return String.format("rgba(%d,%d,%d,%f)", (int) (r * 255), (int) (g * 255), (int) (b * 255), a);
    }

    /**
     * Multiplies this color by a scalar in place.
     *
     * @param scalar number to multiply with
     */
    public SmartColor multiply(double scalar) {
        r *= scalar;
        g *= scalar;
        b *= scalar;
        r = Math.min(1, Math.max(0, r));
        g = Math.min(1, Math.max(0, g));
        b = Math.min(1, Math.max(0, b));
        return this;
    }

    public double brightness() {
        return (r + g + b) / 3;
    }

    /**
     * @return JavaFX version of this color
     */
    public Color toFXColor() {
        return new Color(r, g, b, a);
    }

    public double getRed() {
        return r;
    }

    public void setRed(double r) {
        this.r = r;
    }

    @Override
    public SmartColor clone() {
        return new SmartColor(r, g, b, a);
    }

    public double getGreen() {
        return g;
    }

    public void setGreen(double g) {
        this.g = g;
    }

    public double getBlue() {
        return b;
    }

    public void setBlue(double b) {
        this.b = b;
    }

    public double getAlpha() {
        return a;
    }

    public void setAlpha(double a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmartColor that = (SmartColor) o;

        if (Math.abs(that.r - r) >= 0.00001) return false;
        if (Math.abs(that.g - g) >= 0.00001) return false;
        if (Math.abs(that.b - b) >= 0.00001) return false;
        return Math.abs(that.a - a) < 0.00001;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(r);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(g);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(a);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public SmartColor div(double x) {
        return new SmartColor(r / x, g / x, b / x, a);
    }

    public SmartColor transparentize(double alpha) {
        return new SmartColor(r, g, b, a * alpha);
    }
}
