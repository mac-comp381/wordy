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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;

import wordy.ast.StatementNode;
import wordy.interpreter.EvaluationContext;
import wordy.parser.ParseException;
import wordy.parser.WordyParser;

import static java.awt.Font.PLAIN;

public class Playground {
    private JEditorPane codeEditor, astDump, evalDump;
    private StatementNode currentAST;
    private Executor codeExecutionQueue = Executors.newFixedThreadPool(1);

    public Playground() {
        JFrame window = new JFrame("Wordy IDE");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setSize(1000, screenSize.height);

        codeEditor = new JEditorPane();
        codeEditor.setText(
            "Set a to 1.\n"
            + "Set b to 1.\n"
            + "Set count to 10.\n"
            + "Loop:\n"
            + "    If count is less than 1 then exit loop.\n"
            + "    Set next to a plus b.\n"
            + "    Set a to b.\n"
            + "    Set b to next.\n"
            + "    Set count to count minus 1.\n"
            + "End of loop.\n");

        astDump = new JEditorPane();
        astDump.setEditable(false);

        evalDump = new JEditorPane();
        evalDump.setEditable(false);

        styleTextArea(codeEditor);
        styleTextArea(astDump);
        styleTextArea(evalDump);

        codeEditor.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    codeChanged();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    codeChanged();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    codeChanged();
                }
            }
        );
        codeChanged();

        JTabbedPane outputTabs = new JTabbedPane();
        outputTabs.add("AST", astDump);
        outputTabs.add("Evaluation", evalDump);

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codeEditor, outputTabs);
        mainSplit.setDividerLocation(window.getWidth() / 2);
        window.add(mainSplit);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private void codeChanged() {
        astDump.setForeground(Color.GRAY);
        evalDump.setForeground(Color.GRAY);

        StatementNode ast;
        try {
            codeEditor.getHighlighter().removeAllHighlights();
            ast = WordyParser.parseProgram(codeEditor.getText());
            astDump.setText(ast.dump());
            astDump.setForeground(Color.BLACK);

            reevaluate(ast);
        } catch(ParseException e) {
            ast = null;
            var error = e.getFirstError();
            var errorHighlight = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xFF8866));
            try {
                codeEditor.getHighlighter().addHighlight(
                    Math.max(0, error.getStartIndex() - 1),
                    error.getEndIndex(),
                    errorHighlight);
            } catch (Exception ble) {
                // sometimes highlighting just doesn't work
            }
        } catch(Exception e) {
            ast = null;
        }
    }

    private void reevaluate(StatementNode ast) {
        synchronized(this) {
            currentAST = ast;
        }

        codeExecutionQueue.execute(() -> {
            var context = new EvaluationContext((node, ctx) -> {
                synchronized(this) {
                    if(ast != currentAST)
                        throw new ExecutionCancelledException();
                }
            });

            try {
                ast.run(context);
            } catch(ExecutionCancelledException e) {
                return;
            }

            updateEvalDump(ast, context);
        });
    }

    private void updateEvalDump(StatementNode executingAST, EvaluationContext context) {
        StringBuilder builder = new StringBuilder();
        for(var variableEntry: context.allVariables().entrySet()) {
            builder.append(variableEntry.getKey());
            builder.append(" = ");
            builder.append(variableEntry.getValue());
            builder.append('\n');
        }
        String dump = builder.toString();
        SwingUtilities.invokeLater(() -> {
            evalDump.setText(dump);
            evalDump.setForeground(Color.BLACK);
        });
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
