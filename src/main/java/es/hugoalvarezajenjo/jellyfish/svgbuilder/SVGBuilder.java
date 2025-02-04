package es.hugoalvarezajenjo.jellyfish.svgbuilder;

public class SVGBuilder {
    public static String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    public static String FOOTER = "</svg>\n";

    private final StringBuilder svg = new StringBuilder();

    public SVGBuilder() {
        svg.append(HEADER);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        svg.append("<line x1=\"").append(x1).append("\" y1=\"").append(y1).append("\" x2=\"").append(x2).append("\" y2=\"").append(y2).append("\"/>\n");
    }

    public void drawCircle(int x, int y, int r) {
        svg.append("<circle cx=\"").append(x).append("\" cy=\"").append(y).append("\" r=\"").append(r).append("\"/>\n");
    }

    public void drawRectangle(int x, int y, int width, int height) {
        svg.append("<rect x=\"").append(x).append("\" y=\"").append(y).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>\n");
    }

    public void drawText(int x, int y, String text) {
        svg.append("<text x=\"").append(x).append("\" y=\"").append(y).append("\">").append(text).append("</text>\n");
    }

    public void drawText(int x, int y, String text, double angle) {
        svg.append("<text x=\"").append(x).append("\" y=\"").append(y).append("\" transform=\"rotate(").append(angle).append(")\">").append(text).append("</text>\n");
    }

    public String build() {
        return svg.append(FOOTER).toString();
    }
}
