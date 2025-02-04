package es.hugoalvarezajenjo.jellyfish.parser;

import java.util.LinkedList;
import java.util.List;

public class FishboneNode {
    private final String content;
    private final FishboneNode parent;
    private final List<FishboneNode> children;
    private final int depth;

    public FishboneNode(final String content, final FishboneNode parent, final int depth) {
        this.content = content;
        this.parent = parent;
        this.depth = depth;
        this.children = new LinkedList<>();
    }

    public String getContent() {
        return content;
    }

    public FishboneNode getParent() {
        return parent;
    }

    public List<FishboneNode> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public void addChild(final FishboneNode child) {
        this.children.add(child);
    }
}
