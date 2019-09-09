package wordy.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;

import wordy.ast.ASTNode;
import wordy.parser.ParseException;
import wordy.parser.WordyParser;

import static java.awt.Font.PLAIN;

public class Playground {
    private JFrame window;
    private JEditorPane codePane, outputPane;
    private ASTNode ast;

    public Playground() {
        this.window = new JFrame("Wordy IDE");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setSize(900, screenSize.height);

        codePane = new JEditorPane();
        codePane.setText(
            "Set a to 1.\n"
            + "Set b to 1.\n"
            + "Set count to 10.\n"
            + "Loop:\n"
            + "    If count is less than 1 then exit loop.\n"
            + "    Set next to a plus b.\n"
            + "    Set a to b.\n"
            + "    Set b to next.\n"
            + "    Set count to count minus 1.\n"
            + "End of Loop.\n");

        outputPane = new JEditorPane();
        outputPane.setEditable(false);

        styleTextArea(codePane);
        styleTextArea(outputPane);

        codePane.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    refreshAST();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refreshAST();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refreshAST();
                }
            }
        );
        refreshAST();

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePane, outputPane);
        mainSplit.setDividerLocation(window.getWidth() / 2);
        window.add(mainSplit);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private void refreshAST() {
        try {
            codePane.getHighlighter().removeAllHighlights();
            ast = WordyParser.parseProgram(codePane.getText());
            outputPane.setText(ast.dump());
        } catch(ParseException e) {
            ast = null;
            var error = e.getFirstError();
            var errorHighlight = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xFF8866));
            try {
                codePane.getHighlighter().addHighlight(
                    Math.max(0, error.getStartIndex() - 1),
                    error.getEndIndex(),
                    errorHighlight);
            } catch (Exception ble) {
                // sometimes highlighting just doesn't work
            }
        } catch(Exception e) {
            ast = null;
        }
        outputPane.setForeground(ast == null ? Color.GRAY : Color.BLACK);
    }

    private void styleTextArea(JEditorPane textArea) {
        textArea.setFont(findFont(11, "Zargle", "Menlo", "Consolas", Font.MONOSPACED));
        textArea.setMargin(new Insets(10, 10, 10, 10));
    }

    private Set<String> availableFonts = new HashSet<>(Arrays.asList(
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    private Font findFont(int size, String... namesToTry) {
        for(var name: namesToTry) {
            if(availableFonts.contains(name))
                return new Font(name, PLAIN, size);
        }
        return new JLabel().getFont();  // returns system default
    }

    public static void main(String[] args) {
        new Playground();
    }
}
