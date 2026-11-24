package es.hugoalvarezajenjo.raspa;

import es.hugoalvarezajenjo.raspa.layout.AwtTextMeasurer;
import es.hugoalvarezajenjo.raspa.layout.FishboneLayout;
import es.hugoalvarezajenjo.raspa.layout.Layout;
import es.hugoalvarezajenjo.raspa.parser.FishboneParser;
import es.hugoalvarezajenjo.raspa.parser.ParseException;
import es.hugoalvarezajenjo.raspa.render.Format;
import es.hugoalvarezajenjo.raspa.render.PngRenderer;
import es.hugoalvarezajenjo.raspa.render.SvgRenderer;
import es.hugoalvarezajenjo.raspa.render.Theme;

import java.util.List;

/**
 * Library entry point: source text in, one rendered document per diagram out.
 * Every method throws {@link ParseException}, with the offending line, if the source is invalid.
 */
public final class Raspa {
    private static final FishboneLayout LAYOUT = new FishboneLayout(new AwtTextMeasurer());

    private Raspa() {
    }

    /** SVG documents in the light theme. */
    public static List<String> toSvg(final String source) {
        return toSvg(source, Theme.LIGHT);
    }

    public static List<String> toSvg(final String source, final Theme theme) {
        return layouts(source).stream().map(layout -> SvgRenderer.render(layout, theme)).toList();
    }

    /** PNG images at {@link PngRenderer#SCALE} pixels per layout unit. */
    public static List<byte[]> toPng(final String source, final Theme theme) {
        return render(source, Format.PNG, theme);
    }

    /** The file contents of each diagram in any format, as the CLI writes them. */
    public static List<byte[]> render(final String source, final Format format, final Theme theme) {
        return layouts(source).stream().map(layout -> format.render(layout, theme)).toList();
    }

    private static List<Layout> layouts(final String source) {
        return FishboneParser.parse(source).stream().map(LAYOUT::layout).toList();
    }
}
