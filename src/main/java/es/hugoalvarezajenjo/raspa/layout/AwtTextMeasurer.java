package es.hugoalvarezajenjo.raspa.layout;

import java.awt.font.FontRenderContext;

/** Measures with {@link AwtFonts}, the fonts the SVG asks for and the PNG is drawn with. */
public final class AwtTextMeasurer implements TextMeasurer {
    private static final FontRenderContext CONTEXT = new FontRenderContext(null, true, true);

    @Override
    public double width(final String text, final Role role) {
        return AwtFonts.of(role).getStringBounds(text, CONTEXT).getWidth();
    }
}
