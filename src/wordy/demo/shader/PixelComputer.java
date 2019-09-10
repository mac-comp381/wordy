package wordy.demo.shader;

interface PixelComputer {
    /**
     * Computes the color components for one pixel of an image.
     *
     * @param result Implementations update this with the resulting color
     * @return A report of how much work was done, to let caller space out progress callbacks.
     */
    double computePixel(double x, double y, ColorComponents result);
}
