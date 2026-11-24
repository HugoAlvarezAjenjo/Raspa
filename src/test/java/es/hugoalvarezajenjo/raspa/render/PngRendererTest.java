package es.hugoalvarezajenjo.raspa.render;

import es.hugoalvarezajenjo.raspa.layout.AwtTextMeasurer;
import es.hugoalvarezajenjo.raspa.layout.Box;
import es.hugoalvarezajenjo.raspa.layout.FishboneLayout;
import es.hugoalvarezajenjo.raspa.layout.Layout;
import es.hugoalvarezajenjo.raspa.layout.Point;
import es.hugoalvarezajenjo.raspa.layout.Role;
import es.hugoalvarezajenjo.raspa.layout.Segment;
import es.hugoalvarezajenjo.raspa.parser.FishboneParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Samples pixels where the layout says a shape is, so the tests do not depend on fonts. */
class PngRendererTest {
    private static final Layout LAYOUT = new FishboneLayout(new AwtTextMeasurer())
            .layout(FishboneParser.parse("Effect\n  Category\n    Cause\n").getFirst());

    static Stream<Theme> themes() {
        return Stream.of(Theme.LIGHT, Theme.DARK);
    }

    @ParameterizedTest
    @MethodSource("themes")
    void isAPngOfTheLayoutSizeAtTheScale(final Theme theme) throws IOException {
        final BufferedImage image = render(theme);

        assertEquals(PngRenderer.pixels(LAYOUT.width()), image.getWidth());
        assertEquals(PngRenderer.pixels(LAYOUT.height()), image.getHeight());
    }

    @ParameterizedTest
    @MethodSource("themes")
    void paintsTheBackground(final Theme theme) throws IOException {
        assertEquals(theme.background(), pixel(render(theme), new Point(1, 1)));
    }

    /** The image is rounded up to whole pixels; the last row and column must not be left blank. */
    @ParameterizedTest
    @MethodSource("themes")
    void paintsTheBackgroundUpToTheLastPixel(final Theme theme) throws IOException {
        final BufferedImage image = render(theme);
        final int right = image.getWidth() - 1;
        final int bottom = image.getHeight() - 1;

        assertEquals(theme.background(), new Color(image.getRGB(right, 0)), "last column");
        assertEquals(theme.background(), new Color(image.getRGB(0, bottom)), "last row");
        assertEquals(theme.background(), new Color(image.getRGB(right, bottom)), "corner");
    }

    @ParameterizedTest
    @MethodSource("themes")
    void drawsTheSpineInInk(final Theme theme) throws IOException {
        final Segment spine = LAYOUT.segments().stream().filter(s -> s.role() == Role.EFFECT).findFirst().orElseThrow();
        final Point middle = new Point((spine.from().x() + spine.to().x()) / 2, spine.from().y());

        assertEquals(theme.ink(), pixel(render(theme), middle));
    }

    @ParameterizedTest
    @MethodSource("themes")
    void fillsTheEffectBox(final Theme theme) throws IOException {
        final Box head = LAYOUT.labels().stream().filter(l -> l.role() == Role.EFFECT).findFirst().orElseThrow().box();
        // Inside the outline and the rounded corner, left of the centred text.
        final Point inside = new Point(head.x() + Role.BOX_CORNER_RADIUS + 1, head.center().y());

        assertEquals(theme.boxFill(), pixel(render(theme), inside));
    }

    private static BufferedImage render(final Theme theme) throws IOException {
        final BufferedImage image = ImageIO.read(new ByteArrayInputStream(PngRenderer.render(LAYOUT, theme)));
        assertNotNull(image, "not a readable PNG");
        return image;
    }

    private static Color pixel(final BufferedImage image, final Point p) {
        return new Color(image.getRGB((int) (p.x() * PngRenderer.SCALE), (int) (p.y() * PngRenderer.SCALE)));
    }
}
