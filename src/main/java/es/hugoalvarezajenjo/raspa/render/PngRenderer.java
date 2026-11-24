package es.hugoalvarezajenjo.raspa.render;

import es.hugoalvarezajenjo.raspa.layout.AwtFonts;
import es.hugoalvarezajenjo.raspa.layout.Box;
import es.hugoalvarezajenjo.raspa.layout.Label;
import es.hugoalvarezajenjo.raspa.layout.Layout;
import es.hugoalvarezajenjo.raspa.layout.Role;
import es.hugoalvarezajenjo.raspa.layout.Segment;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/** Draws a positioned diagram as a PNG with Java2D, drawing the same shapes as {@link SvgRenderer}. */
public final class PngRenderer {
    /** Pixels per layout unit, so the image stays sharp on high-density screens. */
    public static final int SCALE = 2;

    private PngRenderer() {
    }

    public static byte[] render(final Layout layout, final Theme theme) {
        final BufferedImage image = new BufferedImage(
                pixels(layout.width()), pixels(layout.height()), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            // In pixels, before scaling: the image is rounded up, so the layout's size would miss the last pixel.
            g.setColor(theme.background());
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.scale(SCALE, SCALE);
            layout.segments().forEach(segment -> line(g, segment, theme));
            layout.labels().forEach(label -> label(g, label, theme));
        } finally {
            g.dispose();
        }
        return png(image);
    }

    /** Image size in pixels for a layout length. */
    public static int pixels(final double length) {
        return (int) Math.ceil(length * SCALE);
    }

    private static void line(final Graphics2D g, final Segment s, final Theme theme) {
        g.setColor(theme.ink());
        g.setStroke(stroke(s.role()));
        g.draw(new Line2D.Double(s.from().x(), s.from().y(), s.to().x(), s.to().y()));
    }

    private static void label(final Graphics2D g, final Label label, final Theme theme) {
        final Box box = label.box();
        final Role role = label.role();
        if (role.boxed()) {
            final double arc = 2 * Role.BOX_CORNER_RADIUS;
            final RoundRectangle2D frame = new RoundRectangle2D.Double(box.x(), box.y(), box.width(), box.height(), arc, arc);
            g.setColor(theme.boxFill());
            g.fill(frame);
            g.setColor(theme.ink());
            g.setStroke(stroke(role));
            g.draw(frame);
        }
        // Centred like the SVG's text-anchor="middle" and dominant-baseline="central".
        final Font font = AwtFonts.of(role);
        final double width = font.getStringBounds(label.text(), g.getFontRenderContext()).getWidth();
        final LineMetrics metrics = font.getLineMetrics(label.text(), g.getFontRenderContext());
        final double baseline = box.center().y() + (metrics.getAscent() - metrics.getDescent()) / 2;
        g.setColor(theme.ink());
        g.setFont(font);
        g.drawString(label.text(), (float) (box.center().x() - width / 2), (float) baseline);
    }

    private static BasicStroke stroke(final Role role) {
        return new BasicStroke((float) role.strokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    private static byte[] png(final BufferedImage image) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (final IOException e) {
            throw new UncheckedIOException("writing to memory cannot fail", e);
        }
        return out.toByteArray();
    }
}
