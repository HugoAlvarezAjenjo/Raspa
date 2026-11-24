package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.CommandLine.UsageException;
import es.hugoalvarezajenjo.raspa.render.Format;
import es.hugoalvarezajenjo.raspa.render.Theme;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLineTest {
    private static final Path FILE = Path.of("a.raspa");

    @Test
    void defaultsToALightSvg() throws UsageException {
        assertEquals(new CommandLine(FILE, Format.SVG, Theme.LIGHT), CommandLine.parse("a.raspa"));
    }

    @Test
    void optionsInAnyPosition() throws UsageException {
        final CommandLine expected = new CommandLine(FILE, Format.PNG, Theme.DARK);

        assertEquals(expected, CommandLine.parse("--png", "--dark-mode", "a.raspa"));
        assertEquals(expected, CommandLine.parse("a.raspa", "--dark-mode", "--png"));
        assertEquals(expected, CommandLine.parse("--dark-mode", "a.raspa", "--png"));
    }

    @Test
    void svgCanBeAskedForExplicitlyAndRepeated() throws UsageException {
        assertEquals(Format.SVG, CommandLine.parse("--svg", "a.raspa").format());
        assertEquals(Format.PNG, CommandLine.parse("--png", "--png", "a.raspa").format());
    }

    @Test
    void rejectsTwoFormats() {
        assertUsageError("choose one of --svg and --png", "--svg", "--png", "a.raspa");
    }

    @Test
    void helpIsRecognisedAnywhere() {
        assertTrue(CommandLine.wantsHelp("-h"));
        assertTrue(CommandLine.wantsHelp("a.raspa", "--help"));
        assertFalse(CommandLine.wantsHelp("a.raspa"));
    }

    @Test
    void rejectsUnknownOptions() {
        assertUsageError("unknown option --dark", "--dark", "a.raspa");
    }

    @Test
    void requiresExactlyOneFile() {
        assertUsageError("missing the diagram file");
        assertUsageError("missing the diagram file", "--dark-mode");
        assertUsageError("expected one file, got a and b", "a", "b");
    }

    private static void assertUsageError(final String message, final String... args) {
        assertEquals(message, assertThrows(UsageException.class, () -> CommandLine.parse(args)).getMessage());
    }
}
