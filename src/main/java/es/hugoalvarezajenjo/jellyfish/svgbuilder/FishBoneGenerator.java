package es.hugoalvarezajenjo.jellyfish.svgbuilder;

import es.hugoalvarezajenjo.jellyfish.Coordinate;
import es.hugoalvarezajenjo.jellyfish.parser.FishboneNode;

public class FishBoneGenerator {
    public static String generate(final FishboneNode node) {
        final SVGBuilder builder = new SVGBuilder();
        generate(node, new Coordinate(0, 0), builder);
        return builder.build();
    }

    private static void generate(final FishboneNode node, final Coordinate origin, final SVGBuilder builder) {
        Coordinate growingVector;
        if (node.getDepth() % 2 == 0) {
            growingVector = new Coordinate(50, 0);
        } else {
            growingVector = new Coordinate(0, 50);
        }
        Coordinate growingVertex = origin.add(growingVector);
        for (int i = 0; i < node.getChildren().size(); i++) {
            FishboneNode child = node.getChildren().get(i);
            generate(child, growingVertex, builder);
            //TODO: growingVertex
        }
        builder.drawLine(origin.getX(), origin.getY(), growingVertex.getX(), growingVertex.getY());
        //TODO: Draw Name
    }
}
