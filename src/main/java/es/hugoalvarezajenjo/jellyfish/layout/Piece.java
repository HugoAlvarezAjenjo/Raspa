package es.hugoalvarezajenjo.jellyfish.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/** An immutable group of bones and labels in its own local coordinates. */
record Piece(List<Segment> segments, List<Label> labels) {
    static final Piece EMPTY = new Piece(List.of(), List.of());

    static Piece of(final Segment segment) {
        return new Piece(List.of(segment), List.of());
    }

    static Piece of(final Label label) {
        return new Piece(List.of(), List.of(label));
    }

    static Piece of(final Segment segment, final Label label) {
        return new Piece(List.of(segment), List.of(label));
    }

    Piece with(final Piece other) {
        return new Piece(concat(segments, other.segments), concat(labels, other.labels));
    }

    Piece translate(final double dx, final double dy) {
        return map(p -> p.translate(dx, dy));
    }

    /** Flips the piece across the line y = 0, to hang it below the spine. */
    Piece mirrorY() {
        return map(Point::mirrorY);
    }

    Box bounds() {
        return Stream.concat(
                        segments.stream().map(s -> Box.spanning(s.from(), s.to())),
                        labels.stream().map(Label::box))
                .reduce(Box::union)
                .orElseThrow();
    }

    private Piece map(final UnaryOperator<Point> transform) {
        return new Piece(
                segments.stream().map(s -> s.map(transform)).toList(),
                labels.stream().map(l -> l.map(transform)).toList());
    }

    private static <T> List<T> concat(final List<T> a, final List<T> b) {
        final List<T> all = new ArrayList<>(a);
        all.addAll(b);
        return all;
    }
}
