package es.hugoalvarezajenjo.raspa.layout;

import es.hugoalvarezajenjo.raspa.model.FishboneNode;
import es.hugoalvarezajenjo.raspa.parser.FishboneParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks geometric properties instead of exact coordinates, so tuning the
 * spacing constants does not break the tests.
 */
class FishboneLayoutTest {
    private static final double EPSILON = 1e-6;
    private static final FishboneLayout LAYOUT = new FishboneLayout((text, role) -> text.length() * role.fontSize() * 0.6);

    private static final String PHOTO = """
            Blurry photo
              Process
                Out of focus
                Shutter speed too slow
              User
                Shaky hands
              Equipment
                Lens
                  Inappropriate
                  Damaged
                Sensor
              Environment
                Too dark
              Settings
            """;

    private static final String DEEP = """
            Effect
              Category A
                Cause 1
                  Sub 1.1
                    Leaf 1.1.1
                    Leaf 1.1.2
                  Sub 1.2
                Cause 2
                  Sub 2.1
              Category B
                Cause 3
                  Sub 3.1
                    Leaf 3.1.1
            """;

    private static final String WIDE = """
            A rather long effect label
              One
                a fairly long cause label
                b
              Two
                c
              Three
              Four
                d
                e
                f
              Five
                g
            """;

    @ParameterizedTest
    @ValueSource(strings = {PHOTO, DEEP, WIDE, "Only an effect"})
    void staysInsideTheCanvas(final String source) {
        final Layout layout = layout(source);
        for (final Segment s : layout.segments()) {
            assertInside(layout, s.from());
            assertInside(layout, s.to());
        }
        for (final Label l : layout.labels()) {
            assertInside(layout, new Point(l.box().x(), l.box().y()));
            assertInside(layout, new Point(l.box().maxX(), l.box().maxY()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {PHOTO, DEEP, WIDE})
    void labelsDoNotOverlap(final String source) {
        final List<Label> labels = layout(source).labels();
        for (int i = 0; i < labels.size(); i++) {
            for (int j = i + 1; j < labels.size(); j++) {
                assertTrue(!rect(labels.get(i).box()).intersects(rect(labels.get(j).box())),
                        labels.get(i).text() + " overlaps " + labels.get(j).text());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {PHOTO, DEEP, WIDE})
    void bonesDoNotCrossLabels(final String source) {
        final Layout layout = layout(source);
        for (final Segment s : layout.segments()) {
            for (final Label l : layout.labels()) {
                assertTrue(!line(s).intersects(shrunk(l.box())), "a " + s.role() + " bone crosses " + l.text());
            }
        }
    }

    /** Bones may only touch where one attaches to the other, never cross. */
    @ParameterizedTest
    @ValueSource(strings = {PHOTO, DEEP, WIDE})
    void bonesDoNotCross(final String source) {
        final List<Segment> segments = layout(source).segments();
        for (int i = 0; i < segments.size(); i++) {
            for (int j = i + 1; j < segments.size(); j++) {
                final Segment a = segments.get(i);
                final Segment b = segments.get(j);
                if (line(a).intersectsLine(line(b))) {
                    assertTrue(touchesAtAnEnd(a, b), "bones cross: " + a + " and " + b);
                }
            }
        }
    }

    @Test
    void headIsTheRightmostElementAndTheSpineIsHorizontal() {
        final Layout layout = layout(PHOTO);
        final Label head = label(layout, "Blurry photo");
        final Segment spine = spine(layout);

        assertEquals(spine.from().y(), spine.to().y(), EPSILON);
        assertEquals(head.box().center().y(), spine.from().y(), EPSILON);
        for (final Segment s : layout.segments()) {
            assertTrue(Math.max(s.from().x(), s.to().x()) <= head.box().x() + EPSILON);
        }
    }

    @Test
    void categoriesAlternateAboveAndBelowTheSpine() {
        final Layout layout = layout(PHOTO);
        final double spineY = spine(layout).from().y();

        assertTrue(label(layout, "Process").box().maxY() < spineY);
        assertTrue(label(layout, "User").box().y() > spineY);
        assertTrue(label(layout, "Equipment").box().maxY() < spineY);
        assertTrue(label(layout, "Environment").box().y() > spineY);
        assertTrue(label(layout, "Settings").box().maxY() < spineY);
    }

    @Test
    void firstCategoriesSitNearestTheHead() {
        final Layout layout = layout(PHOTO);

        assertTrue(label(layout, "Process").box().x() > label(layout, "Equipment").box().x());
        assertTrue(label(layout, "Equipment").box().x() > label(layout, "Settings").box().x());
    }

    @Test
    void orientationAlternatesWithDepth() {
        final Layout layout = layout(DEEP);

        assertSlanted(boneEndingAt(layout, "Category A"));
        assertHorizontal(boneEndingAt(layout, "Cause 1"));
        assertSlanted(boneEndingAt(layout, "Sub 1.1"));
        assertHorizontal(boneEndingAt(layout, "Leaf 1.1.1"));
    }

    @Test
    void onlyTheEffectAndCategoriesAreBoxed() {
        final Layout layout = layout(DEEP);

        assertEquals(Role.EFFECT, label(layout, "Effect").role());
        assertEquals(Role.CATEGORY, label(layout, "Category B").role());
        assertEquals(Role.CAUSE, label(layout, "Cause 3").role());
        assertEquals(Role.CAUSE, label(layout, "Leaf 3.1.1").role());
    }

    private static Layout layout(final String source) {
        final FishboneNode effect = FishboneParser.parse(source).getFirst();
        return LAYOUT.layout(effect);
    }

    private static Label label(final Layout layout, final String text) {
        return layout.labels().stream().filter(l -> l.text().equals(text)).findFirst().orElseThrow();
    }

    private static Segment spine(final Layout layout) {
        return layout.segments().stream().filter(s -> s.role() == Role.EFFECT).findFirst().orElseThrow();
    }

    /** The bone whose free end is nearest to the label. */
    private static Segment boneEndingAt(final Layout layout, final String text) {
        final Point center = label(layout, text).box().center();
        return layout.segments().stream()
                .min((a, b) -> Double.compare(distance(a.to(), center), distance(b.to(), center)))
                .orElseThrow();
    }

    private static void assertSlanted(final Segment s) {
        assertTrue(Math.abs(s.from().y() - s.to().y()) > EPSILON && Math.abs(s.from().x() - s.to().x()) > EPSILON,
                "expected slanted: " + s);
    }

    private static void assertHorizontal(final Segment s) {
        assertEquals(s.from().y(), s.to().y(), EPSILON, "expected horizontal: " + s);
    }

    private static void assertInside(final Layout layout, final Point p) {
        assertTrue(p.x() >= -EPSILON && p.x() <= layout.width() + EPSILON, "x out of canvas: " + p);
        assertTrue(p.y() >= -EPSILON && p.y() <= layout.height() + EPSILON, "y out of canvas: " + p);
    }

    /** ptSegDist takes a square root, so rounding noise grows to about 1e-6 px. */
    private static boolean touchesAtAnEnd(final Segment a, final Segment b) {
        final double touch = 1e-3;
        return line(b).ptSegDist(a.from().x(), a.from().y()) < touch
                || line(b).ptSegDist(a.to().x(), a.to().y()) < touch
                || line(a).ptSegDist(b.from().x(), b.from().y()) < touch
                || line(a).ptSegDist(b.to().x(), b.to().y()) < touch;
    }

    private static double distance(final Point a, final Point b) {
        return Math.hypot(a.x() - b.x(), a.y() - b.y());
    }

    private static Line2D line(final Segment s) {
        return new Line2D.Double(s.from().x(), s.from().y(), s.to().x(), s.to().y());
    }

    private static Rectangle2D rect(final Box box) {
        return new Rectangle2D.Double(box.x(), box.y(), box.width(), box.height());
    }

    /** A bone may end on a box's edge (the spine on the head), so test the interior only. */
    private static Rectangle2D shrunk(final Box box) {
        return new Rectangle2D.Double(box.x() + 0.5, box.y() + 0.5, box.width() - 1, box.height() - 1);
    }
}
