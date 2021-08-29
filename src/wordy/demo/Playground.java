package wordy.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;

import wordy.ast.StatementNode;
import wordy.compiler.WordyCompiler;
import wordy.interpreter.EvaluationContext;
import wordy.parser.ParseException;
import wordy.parser.WordyParser;

import static java.awt.Font.PLAIN;

/**
 * An interactive UI for exploring Wordy that shows:
 * <ul>
 * <li> the AST for user-entered Wordy code,
 * <li> the execution trace and final output of its interpreted execution, and
 * <li> the Wordy code compiled to Java.
 * </ul>
 */
public class Playground {
    private final JTextArea codeEditor;
    private final JTextArea astDump;
    private final JTextArea interpreterDump;
    private final JTextArea compilerDump;
    private StatementNode currentAST;
    private final Executor codeExecutionQueue = Executors.newFixedThreadPool(1);

    public Playground() {
        JFrame window = new JFrame("Wordy IDE");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setSize(1000, screenSize.height);

        codeEditor = new JTextArea();
        codeEditor.setText(
            """
            Set a to 1.
            Set b to 1.
            Set count to 10.
            Loop:
                If count is less than 1 then exit loop.
                Set next to a plus b.
                Set a to b.
                Set b to next.
                Set count to count minus 1.
            End of loop.
            """);

        astDump = new JTextArea();
        astDump.setEditable(false);

        interpreterDump = new JTextArea();
        interpreterDump.setEditable(false);

        compilerDump = new JTextArea();
        compilerDump.setEditable(false);

        styleTextArea(codeEditor);
        styleTextArea(astDump);
        styleTextArea(interpreterDump);
        styleTextArea(compilerDump);

        JTabbedPane outputTabs = new JTabbedPane();
        outputTabs.add("AST", new JScrollPane(astDump));
        outputTabs.add("Interpreted", new JScrollPane(interpreterDump));
        outputTabs.add("Compiled", new JScrollPane(compilerDump));
        outputTabs.setBackground(new Color(160, 255, 240));
        outputTabs.setForeground(Color.BLACK);

        var mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(codeEditor), outputTabs);
        mainSplit.setDividerLocation(window.getWidth() / 3);
        window.add(mainSplit);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

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
    }

    private void codeChanged() {
        astDump.setForeground(Color.GRAY);
        interpreterDump.setForeground(Color.GRAY);

        StatementNode ast;
        try {
            codeEditor.getHighlighter().removeAllHighlights();
            ast = WordyParser.parseProgram(codeEditor.getText());
            astDump.setText(ast.dump());
            astDump.setForeground(Color.BLACK);

            reevaluate(ast);
        } catch(ParseException e) {
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
            // Don't let internal parser errors kill UI
            e.printStackTrace(System.err);
        }
    }

    private void reevaluate(StatementNode ast) {
        synchronized(this) {
            currentAST = ast;
        }
        codeExecutionQueue.execute(() -> {
            updateCompilerDump(ast);
            updateInterpreterDump(ast);
        });
    }

    private void updateCompilerDump(StatementNode ast) {
        try {
            updateDump(compilerDump, WordyCompiler.compile(ast, "PlaygroundCode", "PlaygroundCodeContext"));
        } catch(Exception e) {
            updateDump(compilerDump, e);
            return;
        }
    }

    private void updateInterpreterDump(StatementNode executingAST) {
        final StringBuilder builder = new StringBuilder();

        var context = new EvaluationContext((node, ctx, phase, result) -> {
            synchronized(Playground.this) {
            if(executingAST != currentAST)
                throw new ExecutionCancelledException();
            }

            builder.append("%-10s".formatted(phase.name()));
            builder.append(node);
            if (result != null) {
                builder.append("\n          → ");
                builder.append(result);
            }
            builder.append("\n");
        });

        try {
            executingAST.run(context);
        } catch(ExecutionCancelledException e) {
            return;
        } catch(Exception e) {
            updateDump(interpreterDump, e);
            return;
        }

        builder.append("\nExecution complete.\n\nResulting EvaluationContext:\n");
        for(var variableEntry: context.allVariables().entrySet()) {
            builder.append("  ");
            builder.append(variableEntry.getKey());
            builder.append(" = ");
            builder.append(variableEntry.getValue());
            builder.append('\n');
        }
        updateDump(interpreterDump, builder.toString());
    }

    private void updateDump(JTextArea view, String text) {
        SwingUtilities.invokeLater(() -> {
            view.setText(text);
            view.setForeground(Color.BLACK);
        });
    }

    private void updateDump(JTextArea view, Exception error) {
        var output = new StringWriter();
        var printer = new PrintWriter(output);
        printer.println();
        printer.println("–––––– FAILED ––––––");
        printer.println();
        error.printStackTrace(printer);
        updateDump(view, output.toString());
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(findFont(11, "Menlo", "Consolas", Font.MONOSPACED));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setLineWrap(false);
    }

    private final Set<String> availableFonts = new HashSet<>(Arrays.asList(
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
