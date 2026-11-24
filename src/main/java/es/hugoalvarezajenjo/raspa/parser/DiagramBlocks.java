package es.hugoalvarezajenjo.raspa.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Splits a source into diagrams.
 *
 * <p>Without markers the whole source is one diagram. If any PlantUML marker
 * appears ({@code @startuml}, {@code @startfishbone}, ...), the source is a
 * {@code .puml} file: each {@code @startfishbone}/{@code @endfishbone} block is a
 * diagram and everything else, PlantUML diagrams included, is ignored. Such a
 * file must have at least one fishbone block.
 */
final class DiagramBlocks {
    static final String START = "@startfishbone";
    static final String END = "@endfishbone";
    private static final Pattern ANY_MARKER = Pattern.compile("@(start|end)\\w+");

    /**
     * @param line  line that owns the block: the {@code @startfishbone} line, or 1 without markers
     * @param lines the block content, without markers
     */
    record Block(int line, List<SourceLine> lines) {
    }

    private DiagramBlocks() {
    }

    static List<Block> split(final List<SourceLine> lines) {
        final boolean hasMarkers = lines.stream().anyMatch(DiagramBlocks::isAnyMarker);
        return hasMarkers ? splitMarked(lines) : List.of(new Block(1, lines));
    }

    private static List<Block> splitMarked(final List<SourceLine> lines) {
        final List<Block> blocks = new ArrayList<>();
        SourceLine start = null;
        List<SourceLine> content = null;
        for (final SourceLine line : lines) {
            if (isMarker(line, START)) {
                if (start != null) {
                    throw new ParseException(line.number(),
                            START + " inside the diagram opened at line " + start.number());
                }
                start = line;
                content = new ArrayList<>();
            } else if (isMarker(line, END)) {
                if (start == null) {
                    throw new ParseException(line.number(), END + " without " + START);
                }
                blocks.add(new Block(start.number(), content));
                start = null;
            } else if (start != null) {
                content.add(line);
            }
        }
        if (start != null) {
            throw new ParseException(start.number(), START + " is never closed with " + END);
        }
        if (blocks.isEmpty()) {
            final int first = lines.stream().filter(DiagramBlocks::isAnyMarker).findFirst().orElseThrow().number();
            throw new ParseException(first, "no " + START + " block found");
        }
        return blocks;
    }

    /** A marker may be followed by a name, as in PlantUML: {@code @startfishbone causes}. */
    private static boolean isMarker(final SourceLine line, final String marker) {
        return firstWord(line).equals(marker);
    }

    /** Any PlantUML marker, such as {@code @startuml} or {@code @endmindmap}. */
    private static boolean isAnyMarker(final SourceLine line) {
        return ANY_MARKER.matcher(firstWord(line)).matches();
    }

    private static String firstWord(final SourceLine line) {
        return line.text().strip().split("\\s+", 2)[0];
    }
}
