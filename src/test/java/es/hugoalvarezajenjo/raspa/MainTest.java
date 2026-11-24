package es.hugoalvarezajenjo.raspa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @TempDir
    Path dir;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Test
    void writesTheSvgNextToTheInput() throws IOException {
        final Path input = write("photo.raspa", "Effect\n  Category\n");

        assertEquals(Main.OK, run(input.toString()));
        assertTrue(Files.readString(dir.resolve("photo.svg")).contains("<svg"));
        assertEquals(dir.resolve("photo.svg") + "\n", out());
    }

    @Test
    void numbersTheSvgsOfSeveralDiagrams() throws IOException {
        final Path input = write("many.puml", """
                @startfishbone
                First
                @endfishbone
                @startfishbone
                Second
                @endfishbone
                """);

        assertEquals(Main.OK, run(input.toString()));
        assertTrue(Files.exists(dir.resolve("many-1.svg")));
        assertTrue(Files.exists(dir.resolve("many-2.svg")));
    }

    @Test
    void wrongNumberOfArgumentsPrintsUsage() {
        assertEquals(Main.USAGE, run());
        assertEquals(Main.USAGE, run("a", "b"));
        assertEquals("Usage: raspa <file>\n".repeat(2), err());
    }

    @Test
    void syntaxErrorNamesTheFileAndLine() throws IOException {
        final Path input = write("bad.raspa", "Effect\n\tTab\n");

        assertEquals(Main.ERROR, run(input.toString()));
        assertEquals(input + ": Line 2: tabs are not allowed for indentation; use spaces\n", err());
    }

    @Test
    void missingFileIsAnErrorWithoutAStackTrace() {
        final Path input = dir.resolve("nope.raspa");

        assertEquals(Main.ERROR, run(input.toString()));
        assertEquals(input + ": file not found\n", err());
    }

    @Test
    void directoryIsAnErrorWithoutAStackTrace() {
        assertEquals(Main.ERROR, run(dir.toString()));
        assertTrue(err().startsWith(dir + ": "), err());
        assertTrue(!err().contains("Exception"), err());
    }

    /** A .puml with PlantUML diagrams only must not be read as one fishbone. */
    @Test
    void plantUmlFileWithoutFishbonesIsAnErrorAndWritesNothing() throws IOException {
        final Path input = write("seq.puml", "@startuml\nAlice -> Bob\n@enduml\n");

        assertEquals(Main.ERROR, run(input.toString()));
        assertTrue(err().contains("no @startfishbone block"), err());
        assertTrue(Files.notExists(dir.resolve("seq.svg")));
    }

    private Path write(final String name, final String content) throws IOException {
        return Files.writeString(dir.resolve(name), content);
    }

    private int run(final String... args) {
        return Main.run(args, new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }
}
