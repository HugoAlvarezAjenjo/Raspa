package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.layout.AwtTextMeasurer;
import es.hugoalvarezajenjo.raspa.layout.FishboneLayout;
import es.hugoalvarezajenjo.raspa.parser.FishboneParser;
import es.hugoalvarezajenjo.raspa.parser.ParseException;
import es.hugoalvarezajenjo.raspa.svg.SvgRenderer;

import java.util.List;

/** Library entry point: source text in, one SVG document per diagram out. */
public final class Raspa {
    private static final FishboneLayout LAYOUT = new FishboneLayout(new AwtTextMeasurer());

    private Raspa() {
    }

    /** @throws ParseException with the offending line if the source is invalid */
    public static List<String> toSvg(final String source) {
        return FishboneParser.parse(source).stream()
                .map(LAYOUT::layout)
                .map(SvgRenderer::render)
                .toList();
    }
}
