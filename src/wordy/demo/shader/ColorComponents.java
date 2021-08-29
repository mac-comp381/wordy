package wordy.demo.shader;

/**
 * Passes colors for individual pixels to and from Wordy-based shaders. This mutable class allows a
 * single shader method to return all three color components without object allocation churn.
 */
class ColorComponents {
    private double red, green, blue;

    public void set(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    private static int normalizeColorComponent(double c) {
        return (int) Math.round(Math.sin(c * Math.PI / 2) * 255) & 0xFF;
    }

    public int toInt() {
        return 0xFF000000
            | (normalizeColorComponent(red) << 16)
            | (normalizeColorComponent(green) << 8)
            | (normalizeColorComponent(blue));
    }
}
