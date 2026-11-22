package es.hugoalvarezajenjo.jellyfish.layout;

import java.util.function.UnaryOperator;

/** Text centered in {@code box}; the box is drawn only if the role is boxed. */
public record Label(String text, Role role, Box box) {

    Label map(final UnaryOperator<Point> transform) {
        return new Label(text, role, box.map(transform));
    }
}
