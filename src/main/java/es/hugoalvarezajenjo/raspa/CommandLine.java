package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.render.Theme;

import java.nio.file.Path;

/**
 * The parsed arguments of {@code raspa}. Option names follow PlantUML's command line.
 *
 * @param input the diagram file
 * @param theme {@link Theme#DARK} with {@code --dark-mode}, otherwise {@link Theme#LIGHT}
 */
record CommandLine(Path input, Theme theme) {
    static final String USAGE = """
            Usage: raspa [options] <file>
              --dark-mode   render in dark mode
              -h, --help    show this help""";

    /** Invalid arguments; the message says what is wrong. */
    static final class UsageException extends Exception {
        UsageException(final String message) {
            super(message);
        }
    }

    static boolean wantsHelp(final String... args) {
        for (final String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                return true;
            }
        }
        return false;
    }

    static CommandLine parse(final String... args) throws UsageException {
        Path input = null;
        Theme theme = Theme.LIGHT;
        for (final String arg : args) {
            if (arg.equals("--dark-mode")) {
                theme = Theme.DARK;
            } else if (arg.startsWith("-")) {
                throw new UsageException("unknown option " + arg);
            } else if (input != null) {
                throw new UsageException("expected one file, got " + input + " and " + arg);
            } else {
                input = Path.of(arg);
            }
        }
        if (input == null) {
            throw new UsageException("missing the diagram file");
        }
        return new CommandLine(input, theme);
    }
}
