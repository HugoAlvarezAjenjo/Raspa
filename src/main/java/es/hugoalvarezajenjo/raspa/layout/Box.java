package es.hugoalvarezajenjo.raspa.layout;

import java.util.function.UnaryOperator;

/** An axis-aligned rectangle; {@code (x, y)} is its top-left corner. */
public record Box(double x, double y, double width, double height) {

    static Box spanning(final Point a, final Point b) {
        return new Box(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()),
                Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    public double maxX() {
        return x + width;
    }

    public double maxY() {
        return y + height;
    }

    public Point center() {
        return new Point(x + width / 2, y + height / 2);
    }

    Box union(final Box other) {
        final double minX = Math.min(x, other.x);
        final double minY = Math.min(y, other.y);
        return new Box(minX, minY, Math.max(maxX(), other.maxX()) - minX, Math.max(maxY(), other.maxY()) - minY);
    }

    /** Maps both corners, so it works for any translation or mirroring. */
    Box map(final UnaryOperator<Point> transform) {
        return spanning(transform.apply(new Point(x, y)), transform.apply(new Point(maxX(), maxY())));
    }
}
