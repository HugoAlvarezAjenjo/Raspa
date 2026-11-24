package es.hugoalvarezajenjo.raspa.layout;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * The AWT font of each role: the first of {@link Role#FONT_FAMILIES} installed here,
 * the same list the SVG asks for, else Java's SansSerif. Text is measured and drawn
 * to PNG with these fonts, so both always agree.
 */
public final class AwtFonts {
    private static final Map<Role, Font> FONTS = load();

    private AwtFonts() {
    }

    public static Font of(final Role role) {
        return FONTS.get(role);
    }

    private static Map<Role, Font> load() {
        final Set<String> installed = Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        final String family = Role.FONT_FAMILIES.stream().filter(installed::contains).findFirst().orElse(Font.SANS_SERIF);
        final Map<Role, Font> fonts = new EnumMap<>(Role.class);
        for (final Role role : Role.values()) {
            fonts.put(role, new Font(family, role.bold() ? Font.BOLD : Font.PLAIN, 1).deriveFont((float) role.fontSize()));
        }
        return fonts;
    }
}
