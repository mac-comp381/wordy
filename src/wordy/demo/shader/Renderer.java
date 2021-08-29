package wordy.demo.shader;

import java.awt.image.BufferedImage;

import wordy.demo.ExecutionCancelledException;

/**
 * Generates an image whose pixel colors come from a Shader.
 */
final class Renderer implements Runnable {
    private final BufferedImage image;
    private final double centerX, centerY, scale;
    private final Shader shader;
    private Runnable progressCallback;

    public Renderer(
        BufferedImage image,
        double centerX,
        double centerY,
        double scale,
        Shader shader
    ) {
        this.image = image;
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;
        this.shader = shader;
    }

    @Override
    public void run() {
        System.out.println("Rendering...");
        System.out.flush();
        var timer = System.currentTimeMillis();
        long nextUpdateTime = 0;

        try {
            ColorComponents color = new ColorComponents();
            int width = image.getWidth(), height = image.getHeight();
            double workSinceProgressReport = 0;
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    double realX = (x - width  / 2) * scale + centerX;
                    double realY = (y - height / 2) * scale + centerY;
                    double workDone = shader.computePixelColor(realX, realY, color);
                    image.setRGB(x, y, color.toInt());

                    workSinceProgressReport += Math.max(workDone, 0) + 40;
                    if(workSinceProgressReport > 100000) {
                        workSinceProgressReport = 0;
                        if (System.currentTimeMillis() > nextUpdateTime) {
                            nextUpdateTime = System.currentTimeMillis() + 50;
                            progressCallback.run();
                        }
                    }
                }
            }
            System.out.println("Done rendering (" + (System.currentTimeMillis() - timer) + "ms)");
            System.out.println();
            progressCallback.run();
        } catch(ExecutionCancelledException e) {
            System.out.println("Rendering cancelled");
            System.out.println();
        }
    }

    public void onProgress(Runnable callback) {
        progressCallback = callback;
    }
}
