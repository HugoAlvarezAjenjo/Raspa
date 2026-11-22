package es.hugoalvarezajenjo.raspa.model;

import java.util.List;

/**
 * One node of a fishbone diagram. The root is the effect (the fish head),
 * its children are the categories (the main bones) and deeper nodes are causes.
 *
 * @param label    text shown in the diagram
 * @param line     1-based source line, for error messages and editor navigation
 * @param children direct causes, in source order
 */
public record FishboneNode(String label, int line, List<FishboneNode> children) {
    public FishboneNode {
        children = List.copyOf(children);
    }
}
