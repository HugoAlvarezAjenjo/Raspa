package es.hugoalvarezajenjo.raspa.layout;

/** Width of a text in the font of its role. An interface so tests can use fixed widths. */
@FunctionalInterface
public interface TextMeasurer {
    double width(String text, Role role);
}
