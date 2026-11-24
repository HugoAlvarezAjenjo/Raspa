package es.hugoalvarezajenjo.raspa.render;

import es.hugoalvarezajenjo.raspa.layout.Box;
import es.hugoalvarezajenjo.raspa.layout.Label;
import es.hugoalvarezajenjo.raspa.layout.Layout;
import es.hugoalvarezajenjo.raspa.layout.Role;
import es.hugoalvarezajenjo.raspa.layout.Segment;

import static es.hugoalvarezajenjo.raspa.render.Xml.empty;
import static es.hugoalvarezajenjo.raspa.render.Xml.escape;
import static es.hugoalvarezajenjo.raspa.render.Xml.open;

/** Draws a positioned diagram as a standalone SVG document. */
public final class SvgRenderer {
    private static final String FONTS = String.join(", ", Role.FONT_FAMILIES) + ", sans-serif";

    private SvgRenderer() {
    }

    public static String render(final Layout layout, final Theme theme) {
        final double w = layout.width();
        final double h = layout.height();
        final StringBuilder svg = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append(open("svg", "xmlns", "http://www.w3.org/2000/svg", "width", w, "height", h,
                        "viewBox", "0 0 " + Xml.number(w) + " " + Xml.number(h), "font-family", FONTS)).append('\n')
                .append(empty("rect", "width", "100%", "height", "100%", "fill", theme.background()))
                .append(open("g", "stroke", theme.ink(), "stroke-linecap", "round")).append('\n');
        layout.segments().forEach(segment -> svg.append(line(segment)));
        svg.append("</g>\n");
        layout.labels().forEach(label -> svg.append(label(label, theme)));
        return svg.append("</svg>\n").toString();
    }

    private static String line(final Segment s) {
        return empty("line", "x1", s.from().x(), "y1", s.from().y(), "x2", s.to().x(), "y2", s.to().y(),
                "stroke-width", s.role().strokeWidth());
    }

    private static String label(final Label label, final Theme theme) {
        final Box box = label.box();
        final Role role = label.role();
        final String frame = role.boxed()
                ? empty("rect", "x", box.x(), "y", box.y(), "width", box.width(), "height", box.height(),
                "rx", Role.BOX_CORNER_RADIUS, "fill", theme.boxFill(), "stroke", theme.ink(),
                "stroke-width", role.strokeWidth())
                : "";
        return frame
                + open("text", "x", box.center().x(), "y", box.center().y(),
                "text-anchor", "middle", "dominant-baseline", "central", "fill", theme.ink(),
                "font-size", role.fontSize(), "font-weight", role.bold() ? "bold" : null)
                + escape(label.text()) + "</text>\n";
    }
}
