package es.hugoalvarezajenjo.jellyfish;

import es.hugoalvarezajenjo.jellyfish.layout.AwtTextMeasurer;
import es.hugoalvarezajenjo.jellyfish.layout.FishboneLayout;
import es.hugoalvarezajenjo.jellyfish.parser.FishboneParser;
import es.hugoalvarezajenjo.jellyfish.parser.ParseException;
import es.hugoalvarezajenjo.jellyfish.svg.SvgRenderer;

import java.util.List;

/** Library entry point: source text in, one SVG document per diagram out. */
public final class Jellyfish {
    private static final FishboneLayout LAYOUT = new FishboneLayout(new AwtTextMeasurer());

    private Jellyfish() {
    }

    /** @throws ParseException with the offending line if the source is invalid */
    public static List<String> toSvg(final String source) {
        return FishboneParser.parse(source).stream()
                .map(LAYOUT::layout)
                .map(SvgRenderer::render)
                .toList();
    }
}
