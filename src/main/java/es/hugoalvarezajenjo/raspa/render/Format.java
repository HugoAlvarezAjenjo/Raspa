package es.hugoalvarezajenjo.raspa.render;

import es.hugoalvarezajenjo.raspa.layout.Layout;

import java.nio.charset.StandardCharsets;

/** An output format: its file extension and how a layout becomes the file's bytes. */
public enum Format {
    SVG("svg") {
        @Override
        public byte[] render(final Layout layout, final Theme theme) {
            return SvgRenderer.render(layout, theme).getBytes(StandardCharsets.UTF_8);
        }
    },
    PNG("png") {
        @Override
        public byte[] render(final Layout layout, final Theme theme) {
            return PngRenderer.render(layout, theme);
        }
    };

    private final String extension;

    Format(final String extension) {
        this.extension = extension;
    }

    /** Without the dot, such as {@code svg}. */
    public String extension() {
        return extension;
    }

    public abstract byte[] render(Layout layout, Theme theme);
}
