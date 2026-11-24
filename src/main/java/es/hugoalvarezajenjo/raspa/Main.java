package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.CommandLine.UsageException;
import es.hugoalvarezajenjo.raspa.parser.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * {@code raspa diagram.raspa} writes {@code diagram.svg}, or {@code diagram-N.svg} for several
 * diagrams; {@code --png} writes {@code .png} instead. See {@link CommandLine#USAGE}.
 */
public class Main {
    static final int OK = 0;
    static final int ERROR = 1;
    static final int USAGE = 2;

    public static void main(final String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.exit(run(args, System.out, System.err));
    }

    /** Runs the CLI and returns its exit code; prints written files to {@code out}, problems to {@code err}. */
    static int run(final String[] args, final PrintStream out, final PrintStream err) {
        if (CommandLine.wantsHelp(args)) {
            out.println(CommandLine.USAGE);
            return OK;
        }
        final CommandLine command;
        try {
            command = CommandLine.parse(args);
        } catch (final UsageException e) {
            err.println("raspa: " + e.getMessage());
            err.println(CommandLine.USAGE);
            return USAGE;
        }
        final Path input = command.input();
        try {
            final List<byte[]> files = Raspa.render(Files.readString(input), command.format(), command.theme());
            final String base = input.getFileName().toString().replaceFirst("\\.[^.]*$", "");
            final String extension = "." + command.format().extension();
            for (int i = 0; i < files.size(); i++) {
                final String name = files.size() == 1 ? base + extension : base + "-" + (i + 1) + extension;
                final Path output = input.resolveSibling(name);
                Files.write(output, files.get(i));
                out.println(output);
            }
            return OK;
        } catch (final ParseException e) {
            err.println(input + ": " + e.getMessage());
            return ERROR;
        } catch (final NoSuchFileException e) {
            err.println(input + ": file not found");
            return ERROR;
        } catch (final IOException e) {
            err.println(input + ": " + e.getMessage());
            return ERROR;
        }
    }
}
