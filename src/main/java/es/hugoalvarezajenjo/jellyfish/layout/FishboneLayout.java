package es.hugoalvarezajenjo.jellyfish.layout;

import es.hugoalvarezajenjo.jellyfish.model.FishboneNode;

import java.util.List;

/**
 * Positions a fishbone. The effect is the head on the right and the spine runs
 * left from it. Categories leave the spine as slanted bones, alternating above
 * and below. From there the orientation alternates with depth: causes are
 * horizontal bones on a slanted parent, their causes slanted bones on a
 * horizontal parent, and so on.
 *
 * <p>Every subtree is laid out first in its own frame, with its attach point at
 * the origin and growing upwards and to the left. Its parent then places it:
 * bones along a horizontal parent are {@link Packer packed} into the gaps their
 * neighbours leave, causes along a slanted parent are stacked by bounding box.
 * Bones below the spine are the same layout mirrored.
 */
public final class FishboneLayout {
    /** Horizontal run per unit of rise on slanted bones (about 60 degrees). */
    private static final double SLOPE = 0.6;
    /** Space between sibling subtrees. */
    private static final double GAP = 14;
    /** Space between a bone's end and its label. */
    private static final double LABEL_GAP = 6;
    private static final double BOX_PADDING = 6;
    private static final double MIN_BONE = 30;
    private static final double MARGIN = 20;

    private static final Point ORIGIN = new Point(0, 0);

    private final TextMeasurer measurer;

    public FishboneLayout(final TextMeasurer measurer) {
        this.measurer = measurer;
    }

    public Layout layout(final FishboneNode effect) {
        final Piece fish = spine(effect);
        final Box bounds = fish.bounds();
        final Piece placed = fish.translate(MARGIN - bounds.x(), MARGIN - bounds.y());
        return new Layout(bounds.width() + 2 * MARGIN, bounds.height() + 2 * MARGIN,
                placed.segments(), placed.labels());
    }

    /** The head sits right of the origin; categories alternate above and below the spine. */
    private Piece spine(final FishboneNode effect) {
        final Size head = labelSize(effect.label(), Role.EFFECT);
        Piece fish = Piece.of(
                new Label(effect.label(), Role.EFFECT, new Box(0, -head.height() / 2, head.width(), head.height())));

        final Packer packer = new Packer(GAP);
        packer.occupy(fish);
        for (int i = 0; i < effect.children().size(); i++) {
            final Piece bone = slanted(effect.children().get(i), 1);
            fish = fish.with(pack(packer, i % 2 == 0 ? bone : bone.mirrorY()));
        }
        final double tail = Math.min(fish.bounds().x(), -MIN_BONE);
        return fish.with(Piece.of(new Segment(ORIGIN, new Point(tail, 0), Role.EFFECT)));
    }

    /**
     * Places the piece as far right as the packer allows, never past {@code -GAP}
     * so it stays left of its parent's attach point, and marks it occupied.
     */
    private static Piece pack(final Packer packer, final Piece piece) {
        final Piece placed = piece.translate(packer.fit(piece, -GAP - piece.bounds().maxX()), 0);
        packer.occupy(placed);
        return placed;
    }

    /**
     * A bone rising to the upper left. Its causes are horizontal bones stacked
     * along it, the first nearest the attach point; the label sits above the tip.
     */
    private Piece slanted(final FishboneNode node, final int depth) {
        Piece piece = Piece.EMPTY;
        double rise = GAP;
        for (final FishboneNode child : node.children()) {
            final Piece cause = horizontal(child, depth + 1);
            final Box bounds = cause.bounds();
            final double y = rise + bounds.maxY();
            piece = piece.with(cause.translate(-SLOPE * y, -y));
            rise = y - bounds.y() + GAP;
        }
        rise = Math.max(rise, MIN_BONE);

        final Role role = Role.forDepth(depth);
        final Point tip = new Point(-SLOPE * rise, -rise);
        final Size size = labelSize(node.label(), role);
        final Box box = new Box(tip.x() - size.width() / 2, tip.y() - LABEL_GAP - size.height(),
                size.width(), size.height());
        return piece.with(Piece.of(new Segment(ORIGIN, tip, role), new Label(node.label(), role, box)));
    }

    /**
     * A bone running left. Its causes are slanted bones packed along it, clear of
     * the slanted parent that passes through the attach point; the label sits past
     * the left end.
     */
    private Piece horizontal(final FishboneNode node, final int depth) {
        final List<Piece> causes = node.children().stream().map(c -> slanted(c, depth + 1)).toList();
        final double tallest = causes.stream().mapToDouble(c -> -c.bounds().y()).max().orElse(0);
        final Packer packer = new Packer(GAP);
        final Point parentAbove = new Point(-SLOPE * (tallest + GAP), -(tallest + GAP));
        packer.occupy(Piece.of(new Segment(ORIGIN, parentAbove, Role.forDepth(depth - 1))));
        Piece piece = Piece.EMPTY;
        for (final Piece cause : causes) {
            piece = piece.with(pack(packer, cause));
        }
        final double childrenLeft = causes.isEmpty() ? 0 : piece.bounds().x() - GAP;
        final double end = Math.min(childrenLeft, -MIN_BONE);

        final Role role = Role.forDepth(depth);
        final Size size = labelSize(node.label(), role);
        final Box box = new Box(end - LABEL_GAP - size.width(), -size.height() / 2, size.width(), size.height());
        return piece.with(Piece.of(new Segment(ORIGIN, new Point(end, 0), role), new Label(node.label(), role, box)));
    }

    private record Size(double width, double height) {
    }

    /** Size of the label's box, including padding for boxed roles. */
    private Size labelSize(final String text, final Role role) {
        final double padding = role.boxed() ? 2 * BOX_PADDING : 0;
        return new Size(measurer.width(text, role) + padding, role.lineHeight() + padding);
    }
}
