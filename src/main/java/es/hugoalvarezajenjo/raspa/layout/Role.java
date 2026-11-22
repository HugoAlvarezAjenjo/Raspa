package es.hugoalvarezajenjo.raspa.layout;

import java.util.List;

/**
 * What a bone or label is in the fish. Holds the typography and stroke, so the
 * layout (which measures text) and the renderer (which draws it) cannot disagree.
 */
public enum Role {
    EFFECT(18, true, 3),
    CATEGORY(15, true, 2),
    CAUSE(13, false, 1.2);

    /** Preferred fonts, in order; the SVG adds the generic sans-serif as a last resort. */
    public static final List<String> FONT_FAMILIES = List.of("Helvetica", "Arial");

    private final double fontSize;
    private final boolean bold;
    private final double strokeWidth;

    Role(final double fontSize, final boolean bold, final double strokeWidth) {
        this.fontSize = fontSize;
        this.bold = bold;
        this.strokeWidth = strokeWidth;
    }

    public double fontSize() {
        return fontSize;
    }

    public boolean bold() {
        return bold;
    }

    public double strokeWidth() {
        return strokeWidth;
    }

    public double lineHeight() {
        return fontSize * 1.25;
    }

    /** The effect and the categories are drawn inside a box; causes are plain text. */
    public boolean boxed() {
        return this != CAUSE;
    }

    static Role forDepth(final int depth) {
        return switch (depth) {
            case 0 -> EFFECT;
            case 1 -> CATEGORY;
            default -> CAUSE;
        };
    }
}
