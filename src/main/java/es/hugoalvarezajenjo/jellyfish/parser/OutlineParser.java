package es.hugoalvarezajenjo.jellyfish.parser;

import es.hugoalvarezajenjo.jellyfish.model.FishboneNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses one diagram written as an indented outline.
 *
 * <p>The first line is the effect. The following lines are causes, nested by
 * indentation. The causes' top level may be indented more than the effect or at
 * the same column (Mermaid style). Each level can use any number of spaces, but
 * a line going back out must match an outer level exactly. Blank lines and
 * comments (lines starting with {@code '}) are ignored.
 */
final class OutlineParser {
    private static final String COMMENT = "'";

    private final List<SourceLine> lines;
    private int next;

    private OutlineParser(final List<SourceLine> lines) {
        this.lines = lines;
    }

    static FishboneNode parse(final DiagramBlocks.Block block) {
        final List<SourceLine> content = block.lines().stream()
                .filter(line -> !isBlankOrComment(line))
                .toList();
        if (content.isEmpty()) {
            throw new ParseException(block.line(), "the diagram is empty; its first line must be the effect");
        }
        return new OutlineParser(content).parseEffect();
    }

    private FishboneNode parseEffect() {
        final SourceLine effect = lines.get(next++);
        final List<FishboneNode> causes = new ArrayList<>();
        if (next < lines.size()) {
            final int causeIndent = indent(lines.get(next));
            if (causeIndent < indent(effect)) {
                throw new ParseException(lines.get(next).number(), "causes cannot be indented less than the effect");
            }
            causes.addAll(parseLevel(causeIndent));
            if (next < lines.size()) {
                throw inconsistentIndent(lines.get(next));
            }
        }
        return new FishboneNode(label(effect), effect.number(), causes);
    }

    /** Parses the siblings at {@code levelIndent}; stops at the first line indented less. */
    private List<FishboneNode> parseLevel(final int levelIndent) {
        final List<FishboneNode> nodes = new ArrayList<>();
        while (next < lines.size()) {
            final SourceLine line = lines.get(next);
            final int indent = indent(line);
            if (indent < levelIndent) {
                break;
            }
            if (indent > levelIndent) {
                throw inconsistentIndent(line);
            }
            next++;
            final List<FishboneNode> children = next < lines.size() && indent(lines.get(next)) > indent
                    ? parseLevel(indent(lines.get(next)))
                    : List.of();
            nodes.add(new FishboneNode(label(line), line.number(), children));
        }
        return nodes;
    }

    private static ParseException inconsistentIndent(final SourceLine line) {
        return new ParseException(line.number(), "indentation does not match any outer level");
    }

    private static int indent(final SourceLine line) {
        final String text = line.text();
        int i = 0;
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
            if (text.charAt(i) == '\t') {
                throw new ParseException(line.number(), "tabs are not allowed for indentation; use spaces");
            }
            i++;
        }
        return i;
    }

    private static String label(final SourceLine line) {
        return line.text().strip();
    }

    private static boolean isBlankOrComment(final SourceLine line) {
        final String text = line.text().strip();
        return text.isEmpty() || text.startsWith(COMMENT);
    }
}
