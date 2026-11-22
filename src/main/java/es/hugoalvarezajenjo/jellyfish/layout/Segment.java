package es.hugoalvarezajenjo.jellyfish.layout;

import java.util.function.UnaryOperator;

/** A straight bone. {@code from} is where it attaches to its parent bone. */
public record Segment(Point from, Point to, Role role) {

    Segment map(final UnaryOperator<Point> transform) {
        return new Segment(transform.apply(from), transform.apply(to), role);
    }
}
