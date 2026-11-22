package es.hugoalvarezajenjo.raspa.parser;

/** Invalid source, with the 1-based line where the problem is. */
public final class ParseException extends RuntimeException {
    private final int line;

    ParseException(final int line, final String message) {
        super("Line " + line + ": " + message);
        this.line = line;
    }

    public int line() {
        return line;
    }
}
