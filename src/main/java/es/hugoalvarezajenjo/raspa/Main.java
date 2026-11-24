package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** {@code raspa diagram.raspa} writes {@code diagram.svg}, or {@code diagram-N.svg} for several diagrams. */
public class Main {
    public static void main(final String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        if (args.length != 1) {
            System.err.println("Usage: raspa <file>");
            System.exit(2);
        }
        final Path input = Path.of(args[0]);
        final List<String> svgs;
        try {
            svgs = Raspa.toSvg(Files.readString(input));
        } catch (final ParseException e) {
            System.err.println(input + ": " + e.getMessage());
            System.exit(1);
            return;
        }
        final String base = input.getFileName().toString().replaceFirst("\\.[^.]*$", "");
        for (int i = 0; i < svgs.size(); i++) {
            final String name = svgs.size() == 1 ? base + ".svg" : base + "-" + (i + 1) + ".svg";
            final Path output = input.resolveSibling(name);
            Files.writeString(output, svgs.get(i));
            System.out.println(output);
        }
    }
}
