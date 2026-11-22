package es.hugoalvarezajenjo.jellyfish.layout;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackerTest {
    private static final double GAP = 10;

    @Test
    void withNothingOccupiedThePieceStaysAtTheLimit() {
        assertEquals(-5, new Packer(GAP).fit(box(0, 0, 10, 10), -5), 1e-9);
    }

    @Test
    void keepsTheGapToAnOccupiedBoxOnTheSameRows() {
        final Packer packer = new Packer(GAP);
        packer.occupy(box(100, 0, 20, 10));

        assertEquals(100 - GAP - 10, packer.fit(box(0, 0, 10, 10), 1000), 1e-9);
    }

    @Test
    void ignoresOccupiedShapesOnOtherRows() {
        final Packer packer = new Packer(GAP);
        packer.occupy(box(100, 0, 20, 10));

        assertEquals(1000, packer.fit(box(0, 50, 10, 10), 1000), 1e-9);
    }

    /** The point of the packer: a box tucks under a slanted bone instead of clearing its bounding box. */
    @Test
    void tucksUnderASlantedBone() {
        final Packer packer = new Packer(GAP);
        packer.occupy(new Piece(List.of(new Segment(new Point(100, 100), new Point(40, 0), Role.CATEGORY)), List.of()));

        final double dx = packer.fit(box(0, 80, 10, 10), 1000);

        assertTrue(dx + 10 > 40, "should pass the bone's bounding box, which starts at x=40");
        // The box spans rows 80..90, padded by GAP/2 to 75..95, where the bone is at x >= 40 + 0.6 * 75 = 85.
        assertTrue(dx + 10 <= 85 - GAP + 1e-9, "but stay clear of the bone on its rows");
    }

    private static Piece box(final double x, final double y, final double width, final double height) {
        return new Piece(List.of(), List.of(new Label("", Role.CAUSE, new Box(x, y, width, height))));
    }
}
