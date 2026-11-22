package es.hugoalvarezajenjo.jellyfish.layout;

import java.util.List;

/** A positioned diagram, everything inside {@code [0, width] x [0, height]}. */
public record Layout(double width, double height, List<Segment> segments, List<Label> labels) {
    public Layout {
        segments = List.copyOf(segments);
        labels = List.copyOf(labels);
    }
}
