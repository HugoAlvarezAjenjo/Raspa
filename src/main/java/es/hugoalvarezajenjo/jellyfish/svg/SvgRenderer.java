package es.hugoalvarezajenjo.jellyfish.svg;

import es.hugoalvarezajenjo.jellyfish.layout.Box;
import es.hugoalvarezajenjo.jellyfish.layout.Label;
import es.hugoalvarezajenjo.jellyfish.layout.Layout;
import es.hugoalvarezajenjo.jellyfish.layout.Role;
import es.hugoalvarezajenjo.jellyfish.layout.Segment;

import java.util.Locale;

/** Draws a positioned diagram as a standalone SVG document. */
public final class SvgRenderer {
    private static final String INK = "#333333";
    private static final String BOX_FILL = "#f4f6f8";

    private SvgRenderer() {
    }

    public static String render(final Layout layout) {
        final StringBuilder svg = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(n(layout.width()))
                .append("\" height=\"").append(n(layout.height()))
                .append("\" viewBox=\"0 0 ").append(n(layout.width())).append(' ').append(n(layout.height()))
                .append("\" font-family=\"").append(String.join(", ", Role.FONT_FAMILIES)).append(", sans-serif\">\n")
                .append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n")
                .append("<g stroke=\"").append(INK).append("\" stroke-linecap=\"round\">\n");
        layout.segments().forEach(segment -> line(svg, segment));
        svg.append("</g>\n");
        layout.labels().forEach(label -> label(svg, label));
        return svg.append("</svg>\n").toString();
    }

    private static void line(final StringBuilder svg, final Segment segment) {
        svg.append("<line x1=\"").append(n(segment.from().x())).append("\" y1=\"").append(n(segment.from().y()))
                .append("\" x2=\"").append(n(segment.to().x())).append("\" y2=\"").append(n(segment.to().y()))
                .append("\" stroke-width=\"").append(n(segment.role().strokeWidth())).append("\"/>\n");
    }

    private static void label(final StringBuilder svg, final Label label) {
        final Box box = label.box();
        if (label.role().boxed()) {
            svg.append("<rect x=\"").append(n(box.x())).append("\" y=\"").append(n(box.y()))
                    .append("\" width=\"").append(n(box.width())).append("\" height=\"").append(n(box.height()))
                    .append("\" rx=\"4\" fill=\"").append(BOX_FILL).append("\" stroke=\"").append(INK)
                    .append("\" stroke-width=\"").append(n(label.role().strokeWidth())).append("\"/>\n");
        }
        svg.append("<text x=\"").append(n(box.center().x())).append("\" y=\"").append(n(box.center().y()))
                .append("\" text-anchor=\"middle\" dominant-baseline=\"central\" fill=\"").append(INK)
                .append("\" font-size=\"").append(n(label.role().fontSize())).append('"')
                .append(label.role().bold() ? " font-weight=\"bold\"" : "")
                .append('>').append(escape(label.text())).append("</text>\n");
    }

    private static String n(final double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
