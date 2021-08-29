package wordy.parser;

import org.parboiled.errors.ErrorUtils;
import org.parboiled.errors.ParseError;
import org.parboiled.support.ParsingResult;

/**
 * A syntax error in a Wordy program.
 */
public class ParseException extends RuntimeException {
    private final ParsingResult<?> parseResult;

    public ParseException(String message) {
        super(message);
        parseResult = null;
    }

    public ParseException(ParsingResult<?> parseResult) {
        super(ErrorUtils.printParseErrors(parseResult));
        this.parseResult = parseResult;
    }

    public ParsingResult<?> getParseResult() {
        return parseResult;
    }

    public ParseError getFirstError() {
        if(parseResult == null || parseResult.parseErrors.isEmpty())
            return null;
        return parseResult.parseErrors.get(0);
    }
}
