package es.hugoalvarezajenjo.jellyfish;

import es.hugoalvarezajenjo.jellyfish.parser.FishboneParser;
import es.hugoalvarezajenjo.jellyfish.svgbuilder.FishBoneGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(final String[] args) throws IOException {
        final String source = """
                Prueba
                  L1
                    L2
                    L2
                      L3
                      L3
                  L1
                  L1
                """;
        final String svg = FishBoneGenerator.generate(FishboneParser.parse(source).getFirst());
        Files.writeString(Path.of("diagram.svg"), svg);
    }
}
