package es.hugoalvarezajenjo.jellyfish.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a source into diagrams.
 *
 * <p>Without markers the whole source is one diagram. If any
 * {@code @startfishbone}/{@code @endfishbone} marker appears, each block is a
 * diagram and everything outside the blocks is ignored, so fishbones can live in
 * a {@code .puml} file next to PlantUML diagrams.
 */
final class DiagramBlocks {
    static final String START = "@startfishbone";
    static final String END = "@endfishbone";

    /**
     * @param line  line that owns the block: the {@code @startfishbone} line, or 1 without markers
     * @param lines the block content, without markers
     */
    record Block(int line, List<SourceLine> lines) {
    }

    private DiagramBlocks() {
    }

    static List<Block> split(final List<SourceLine> lines) {
        final boolean hasMarkers = lines.stream().anyMatch(l -> isMarker(l, START) || isMarker(l, END));
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
        return blocks;
    }

    /** A marker may be followed by a name, as in PlantUML: {@code @startfishbone causes}. */
    private static boolean isMarker(final SourceLine line, final String marker) {
        final String[] words = line.text().strip().split("\\s+", 2);
        return words[0].equals(marker);
    }
}
