package es.hugoalvarezajenjo.raspa.render;

import java.awt.Color;
import java.util.Locale;

/**
 * Writes XML tags from name/value attribute pairs. Numbers get one decimal,
 * colours become {@code #rrggbb}, strings are escaped, and an attribute whose
 * value is {@code null} is skipped.
 */
final class Xml {
    private Xml() {
    }

    /** {@code <name a="v" ...>} */
    static String open(final String name, final Object... attributes) {
        return "<" + name + attributes(attributes) + ">";
    }

    /** {@code <name a="v" .../>} on its own line. */
    static String empty(final String name, final Object... attributes) {
        return "<" + name + attributes(attributes) + "/>\n";
    }

    static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String attributes(final Object[] pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("attributes must come in name/value pairs");
        }
        final StringBuilder out = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            if (pairs[i + 1] != null) {
                out.append(' ').append(pairs[i]).append("=\"").append(value(pairs[i + 1])).append('"');
            }
        }
        return out.toString();
    }

    static String number(final double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    /** {@code #rrggbb}; alpha is not used by any theme. */
    static String color(final Color color) {
        return String.format(Locale.ROOT, "#%06x", color.getRGB() & 0xffffff);
    }

    private static String value(final Object value) {
        return switch (value) {
            case Double d -> number(d);
            case Color c -> color(c);
            default -> escape(value.toString());
        };
    }
}
