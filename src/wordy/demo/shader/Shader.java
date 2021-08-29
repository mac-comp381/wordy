package wordy.demo.shader;

/**
 * A shader is a program that computes the color of one pixel in an image. GPUs (graphics processing
 * units, sometimes called “graphics cards”) have their own special shader languages, which they use
 * to run custom app-provided shaders in parallel across the many pixels of an image.
 * 
 * This demo app approximates that, treating Wordy as a shader language, and using Wordy programs to
 * generate images.
 */
interface Shader {
    /**
     * Computes the color components for one pixel of an image.
     *
     * @param result Implementations update this with the resulting color
     * @return A report of how much work was done, to let caller space out progress callbacks.
     */
    double computePixelColor(double x, double y, ColorComponents result);
}
