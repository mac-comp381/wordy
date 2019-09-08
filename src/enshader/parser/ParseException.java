package enshader.parser;

import org.parboiled.errors.ErrorUtils;
import org.parboiled.support.ParsingResult;

public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }

    public ParseException(ParsingResult<?> parseResult) {
        super(ErrorUtils.printParseErrors(parseResult));
    }
}
