package es.hugoalvarezajenjo.raspa.parser;

import es.hugoalvarezajenjo.raspa.model.FishboneNode;

import java.util.List;

/**
 * Entry point of the parser: source text in, one effect node per diagram out.
 *
 * <pre>
 * Blurry photo
 *   Equipment
 *     Lens
 *       Dirty
 * </pre>
 *
 * @see DiagramBlocks for files with several diagrams
 * @see OutlineParser for the outline rules
 */
public final class FishboneParser {
    private FishboneParser() {
    }

    /** @throws ParseException with the offending line if the source is invalid */
    public static List<FishboneNode> parse(final String source) {
        return DiagramBlocks.split(SourceLine.split(source)).stream()
                .map(OutlineParser::parse)
                .toList();
    }
}
