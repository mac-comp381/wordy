package wordy.demo.shader;

import java.awt.image.BufferedImage;

import wordy.demo.ExecutionCancelledException;

final class Renderer implements Runnable {
    private final BufferedImage image;
    private final double centerX, centerY, scale;
    private final PixelComputer pixelComputer;
    private Runnable progressCallback;

    public Renderer(
        BufferedImage image,
        double centerX,
        double centerY,
        double scale,
        PixelComputer pixelComputer
    ) {
        this.image = image;
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;
        this.pixelComputer = pixelComputer;
    }

    @Override
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public void run() {
        System.out.println("Rendering...");
        System.out.flush();
        var timer = System.currentTimeMillis();

        try {
            ColorComponents color = new ColorComponents();
            int width = image.getWidth(), height = image.getHeight();
            double workSinceProgressReport = 0;
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    double realX = (x - width  / 2) * scale + centerX;
                    double realY = (y - height / 2) * scale + centerY;
                    double workDone = pixelComputer.computePixel(realX, realY, color);
                    image.setRGB(x, y, color.toInt());

                    workSinceProgressReport += Math.max(workDone, 0) + 40;
                    if(workSinceProgressReport > 100000) {
                        workSinceProgressReport = 0;
                        progressCallback.run();
                    }
                }
            }
            System.out.println("Done rendering (" + (System.currentTimeMillis() - timer) + "ms)");
            System.out.println();
        } catch(ExecutionCancelledException e) {
            System.out.println("Rendering cancelled");
            System.out.println();
        }
    }

    public void onProgress(Runnable callback) {
        progressCallback = callback;
    }
}
