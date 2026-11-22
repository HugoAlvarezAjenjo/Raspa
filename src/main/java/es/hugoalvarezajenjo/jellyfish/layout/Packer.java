package es.hugoalvarezajenjo.jellyfish.layout;

import java.util.HashMap;
import java.util.Map;

/**
 * Packs pieces leftwards: each new piece slides right until it is {@code gap}
 * away from what is already placed, compared band by band (rows of
 * {@link #BAND} px). A piece can thus tuck into the empty triangle left by a
 * slanted neighbour, which bounding boxes alone would waste.
 *
 * <p>Occupied shapes are padded vertically by half the gap, so pieces that end
 * up one above the other keep some vertical space too.
 */
final class Packer {
    static final double BAND = 1;

    @FunctionalInterface
    private interface BandConsumer {
        void accept(int band, double minX, double maxX);
    }

    private final double gap;
    private final Map<Integer, Double> leftmost = new HashMap<>();

    Packer(final double gap) {
        this.gap = gap;
    }

    void occupy(final Piece piece) {
        forEachBand(piece, gap / 2, (band, minX, maxX) -> leftmost.merge(band, minX, Math::min));
    }

    /**
     * The largest horizontal shift, at most {@code maxDx}, that keeps the piece
     * {@code gap} to the left of everything occupied.
     */
    double fit(final Piece piece, final double maxDx) {
        final Map<Integer, Double> rightmost = new HashMap<>();
        forEachBand(piece, 0, (band, minX, maxX) -> rightmost.merge(band, maxX, Math::max));
        double dx = maxDx;
        for (final Map.Entry<Integer, Double> row : rightmost.entrySet()) {
            final Double left = leftmost.get(row.getKey());
            if (left != null) {
                dx = Math.min(dx, left - gap - row.getValue());
            }
        }
        return dx;
    }

    private static void forEachBand(final Piece piece, final double margin, final BandConsumer consumer) {
        piece.segments().forEach(s -> forEachBand(s, margin, consumer));
        piece.labels().forEach(l -> forEachBand(l.box(), margin, consumer));
    }

    private static void forEachBand(final Box box, final double margin, final BandConsumer consumer) {
        for (int band = band(box.y() - margin); band <= band(box.maxY() + margin); band++) {
            consumer.accept(band, box.x(), box.maxX());
        }
    }

    /** Reports the x range the segment covers within each band it crosses. */
    private static void forEachBand(final Segment segment, final double margin, final BandConsumer consumer) {
        final Point a = segment.from();
        final Point b = segment.to();
        final double minY = Math.min(a.y(), b.y());
        final double maxY = Math.max(a.y(), b.y());
        if (a.y() == b.y()) {
            forEachBand(Box.spanning(a, b), margin, consumer);
            return;
        }
        for (int band = band(minY - margin); band <= band(maxY + margin); band++) {
            final double lo = Math.max(minY, band * BAND - margin);
            final double hi = Math.min(maxY, (band + 1) * BAND + margin);
            final double x1 = xAt(a, b, lo);
            final double x2 = xAt(a, b, hi);
            consumer.accept(band, Math.min(x1, x2), Math.max(x1, x2));
        }
    }

    private static double xAt(final Point a, final Point b, final double y) {
        return a.x() + (b.x() - a.x()) * (y - a.y()) / (b.y() - a.y());
    }

    private static int band(final double y) {
        return (int) Math.floor(y / BAND);
    }
}
