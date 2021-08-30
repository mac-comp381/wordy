package wordy.demo.shader;

/**
 * A shader is a program that computes the color of one pixel in an image. GPUs (graphics processing
 * units, sometimes called “graphics cards”) have their own special shader languages, which they use
 * to run custom app-provided shaders in parallel across the many pixels of an image.
 * 
 * This demo app treats Wordy as a shader language, in the spirit of GPU shaders, and uses Wordy
 * programs to generate images: the whole Wordy program implements the logic to compute the color
 * for just one point, and the ShaderUI class runs the shader many times with different coordinates
 * to produce a whole image.
 * 
 * A Wordy shader takes variables named x and y as input, and sets variables named red, greem, and
 * blue as output. The Wordy shader may also use two optional variables: the view_scale input
 * indicates how far the UI is zoomed in, and the work_done output gives an approximation of how
 * much computation time the shader took.
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
