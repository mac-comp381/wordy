package wordy.demo.shader;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import wordy.ast.StatementNode;
import wordy.demo.ExecutionCancelledException;
import wordy.parser.WordyParser;

public class ShaderUI {
    private static final boolean USE_COMPILER = true;

    private final StatementNode program;
    private final BufferedImage image;
    private final JFrame window;
    private double centerX, centerY, scale;

    private Renderer currentRenderer;
    private final Executor renderWorker = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws Exception {
        new ShaderUI(
            "ripples.wordy",  // Simple test involving only assignments and expressions
//            "mandel.wordy",   // More complex test involving loops and conditionals
            600, 600
        );
    }

    public ShaderUI(String sourceFileName, int width, int height) throws Exception {
        program = loadProgram("/" + sourceFileName);

        int pixelRatio = 2;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        window = new ImageWindow(sourceFileName, image, pixelRatio);

        centerX = 0;
        centerY = 0;
        scale = 2.0 / width;

        window.addMouseListener(new MouseAdapter() {
            @Override
            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            public void mouseClicked(MouseEvent e) {
                centerX = (e.getX() * pixelRatio - width  / 2) * scale + centerX;
                centerY = (e.getY() * pixelRatio - height / 2) * scale + centerY;
                if(e.isAltDown()) {
                    scale *= 4;
                } else {
                    scale /= 4;
                }
                render();
            }
        });

        render();
    }

    private static StatementNode loadProgram(String sourceFile) throws IOException, URISyntaxException {
        String source = new String(Files.readAllBytes(Paths.get(
            ShaderUI.class.getResource(sourceFile).toURI())));
        System.out.println(source);
        System.out.println();

        var program = WordyParser.parseProgram(source);
        System.out.println(program.dump());
        System.out.println();

        return program;
    }

    private void render() {
        Renderer renderer = new Renderer(
            image, centerX, centerY, scale,
            USE_COMPILER
                ? new CompiledPixelComputer(program, scale)
                : new InterpretedPixelComputer(program, scale));

        synchronized(this) {
            currentRenderer = renderer;
        }

        renderer.onProgress(() -> {
            window.repaint(100);
            synchronized(this) {
                if(renderer != currentRenderer)
                    throw new ExecutionCancelledException();
            }
        });

        renderWorker.execute(renderer);
    }

    private static class ImageWindow extends JFrame {
        private BufferedImage image;

        public ImageWindow(String title, BufferedImage image, int pixelRatio) throws HeadlessException {
            super(title);
            this.image = image;
            pack();
            setSize(
                image.getWidth()  / pixelRatio + getInsets().left + getInsets().right,
                image.getHeight() / pixelRatio + getInsets().top + getInsets().bottom);
            setResizable(false);
            setVisible(true);
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(image, getInsets().left, getInsets().top, getWidth(), getHeight(), null);
        }
    }
}
