package es.hugoalvarezajenjo.raspa.render;

import java.awt.Color;

/**
 * The colours of a diagram, shared by every renderer so SVG and PNG look the same.
 *
 * @param background canvas
 * @param ink        bones, box outlines and text
 * @param boxFill    inside the effect and category boxes
 */
public record Theme(Color background, Color ink, Color boxFill) {
    public static final Theme LIGHT = new Theme(Color.WHITE, new Color(0x333333), new Color(0xf4f6f8));
    /** Background as in PlantUML's dark mode. */
    public static final Theme DARK = new Theme(new Color(0x1b1b1b), new Color(0xe6e6e6), new Color(0x2b2d31));
}
