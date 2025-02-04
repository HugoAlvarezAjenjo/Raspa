package es.hugoalvarezajenjo.jellyfish;

import es.hugoalvarezajenjo.jellyfish.parser.FishboneParser;
import es.hugoalvarezajenjo.jellyfish.svgbuilder.FishBoneGenerator;

import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        String test = """
                Prueba
                - L1
                -- L2
                -- L2
                --- L3
                --- L3
                - L1
                - L1""";
        String content = FishBoneGenerator.generate(FishboneParser.parse(test));
        try (FileWriter file = new FileWriter("diagram.svg")) {
            file.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}