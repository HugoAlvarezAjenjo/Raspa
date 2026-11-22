package es.hugoalvarezajenjo.jellyfish.layout;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.util.Set;

/**
 * Measures with the first of {@link Role#FONT_FAMILIES} installed here, the same
 * list the SVG asks for, so measured and rendered widths match. Falls back to
 * Java's SansSerif, which is wider; the label gaps absorb small differences.
 */
public final class AwtTextMeasurer implements TextMeasurer {
    private static final FontRenderContext CONTEXT = new FontRenderContext(null, true, true);

    private final String family;

    public AwtTextMeasurer() {
        final Set<String> installed = Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        this.family = Role.FONT_FAMILIES.stream().filter(installed::contains).findFirst().orElse(Font.SANS_SERIF);
    }

    @Override
    public double width(final String text, final Role role) {
        final Font font = new Font(family, role.bold() ? Font.BOLD : Font.PLAIN, 1).deriveFont((float) role.fontSize());
        return font.getStringBounds(text, CONTEXT).getWidth();
    }
}
