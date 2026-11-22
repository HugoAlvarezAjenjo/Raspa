package es.hugoalvarezajenjo.jellyfish.layout;

/** A point in SVG coordinates: x grows to the right, y grows downwards. */
public record Point(double x, double y) {

    Point translate(final double dx, final double dy) {
        return new Point(x + dx, y + dy);
    }

    Point mirrorY() {
        return new Point(x, -y);
    }
}
