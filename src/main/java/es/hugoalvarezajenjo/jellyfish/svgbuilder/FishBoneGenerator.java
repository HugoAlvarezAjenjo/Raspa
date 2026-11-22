package es.hugoalvarezajenjo.jellyfish.svgbuilder;

import es.hugoalvarezajenjo.jellyfish.Coordinate;
import es.hugoalvarezajenjo.jellyfish.model.FishboneNode;

// Placeholder kept compiling against the new model; the layout is rewritten next.
public class FishBoneGenerator {
    public static String generate(final FishboneNode effect) {
        final SVGBuilder builder = new SVGBuilder();
        generate(effect, 0, new Coordinate(0, 0), builder);
        return builder.build();
    }

    private static void generate(final FishboneNode node, final int depth, final Coordinate origin, final SVGBuilder builder) {
        final Coordinate growingVector = depth % 2 == 0 ? new Coordinate(50, 0) : new Coordinate(0, 50);
        final Coordinate growingVertex = origin.add(growingVector);
        for (final FishboneNode child : node.children()) {
            generate(child, depth + 1, growingVertex, builder);
        }
        builder.drawLine(origin.x(), origin.y(), growingVertex.x(), growingVertex.y());
    }
}
