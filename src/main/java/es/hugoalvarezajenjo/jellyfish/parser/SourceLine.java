package es.hugoalvarezajenjo.jellyfish.parser;

import java.util.ArrayList;
import java.util.List;

/** A source line with its 1-based number. */
record SourceLine(int number, String text) {

    static List<SourceLine> split(final String source) {
        final String[] texts = source.split("\\R", -1);
        final List<SourceLine> lines = new ArrayList<>(texts.length);
        for (int i = 0; i < texts.length; i++) {
            lines.add(new SourceLine(i + 1, texts[i]));
        }
        return lines;
    }
}
