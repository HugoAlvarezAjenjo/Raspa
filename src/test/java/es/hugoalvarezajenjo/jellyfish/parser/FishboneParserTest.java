package es.hugoalvarezajenjo.jellyfish.parser;

import es.hugoalvarezajenjo.jellyfish.model.FishboneNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FishboneParserTest {

    @Nested
    class Outline {
        @Test
        void nestsCausesByIndentation() {
            final FishboneNode effect = parseOne("""
                    Blurry photo
                      Process
                        Out of focus
                      Equipment
                        Lens
                          Dirty
                          Damaged
                        Sensor
                      Environment
                    """);

            assertEquals("""
                    Blurry photo
                      Process
                        Out of focus
                      Equipment
                        Lens
                          Dirty
                          Damaged
                        Sensor
                      Environment
                    """, outline(effect));
        }

        @Test
        void acceptsMermaidStyleCategoriesAtTheEffectColumn() {
            final FishboneNode effect = parseOne("""
                        Blurry photo
                        Process
                            Out of focus
                        User
                    """);

            assertEquals("""
                    Blurry photo
                      Process
                        Out of focus
                      User
                    """, outline(effect));
        }

        @Test
        void allowsADifferentIndentWidthPerLevel() {
            final FishboneNode effect = parseOne("""
                    Effect
                      A
                          A1
                      B
                    """);

            assertEquals("""
                    Effect
                      A
                        A1
                      B
                    """, outline(effect));
        }

        @Test
        void ignoresBlankLinesAndCommentsAndTrimsLabels() {
            final FishboneNode effect = parseOne("""
                    ' A comment before the effect
                    Effect  \n\

                      A
                        ' an indented comment
                        A1   \n\
                    """);

            assertEquals("""
                    Effect
                      A
                        A1
                    """, outline(effect));
        }

        @Test
        void effectWithoutCausesIsValid() {
            assertEquals("Effect\n", outline(parseOne("Effect")));
        }

        @Test
        void recordsSourceLineNumbers() {
            final FishboneNode effect = parseOne("\n' comment\nEffect\n  A\n\n    A1\n");

            assertEquals(3, effect.line());
            assertEquals(4, effect.children().getFirst().line());
            assertEquals(6, effect.children().getFirst().children().getFirst().line());
        }

        @Test
        void acceptsWindowsLineEndings() {
            assertEquals("Effect\n  A\n    A1\n", outline(parseOne("Effect\r\n  A\r\n    A1\r\n")));
        }
    }

    @Nested
    class Blocks {
        @Test
        void withoutMarkersTheWholeSourceIsOneDiagram() {
            assertEquals(1, FishboneParser.parse("Effect\n  A\n").size());
        }

        @Test
        void eachMarkedBlockIsADiagramAndTheRestIsIgnored() {
            final List<FishboneNode> diagrams = FishboneParser.parse("""
                    @startuml
                    Alice -> Bob
                    @enduml

                    @startfishbone first
                    First
                      A
                    @endfishbone

                      @startfishbone
                    Second
                      @endfishbone
                    """);

            assertEquals(List.of("First\n  A\n", "Second\n"), diagrams.stream().map(FishboneParserTest::outline).toList());
            assertEquals(6, diagrams.getFirst().line());
        }

        @Test
        void markerPrefixAloneIsNotAMarker() {
            final FishboneNode effect = parseOne("Effect\n  @startfishbonex\n");

            assertEquals("Effect\n  @startfishbonex\n", outline(effect));
        }
    }

    @Nested
    class Errors {
        @Test
        void emptySource() {
            assertError(1, "the diagram is empty", "\n' only a comment\n");
        }

        @Test
        void emptyBlockPointsAtItsStartMarker() {
            assertError(2, "the diagram is empty", "text\n@startfishbone\n\n@endfishbone\n");
        }

        @Test
        void tabIndentation() {
            assertError(2, "tabs are not allowed", "Effect\n\tA\n");
        }

        @Test
        void secondEffect() {
            assertError(3, "indentation does not match", "Effect\n  A\nOther effect\n");
        }

        @Test
        void dedentToAColumnThatIsNotAnOuterLevel() {
            assertError(4, "indentation does not match", "Effect\n  A\n      A1\n    A2\n");
        }

        @Test
        void causesIndentedLessThanTheEffect() {
            assertError(2, "causes cannot be indented less than the effect", "    Effect\n  A\n");
        }

        @Test
        void endMarkerWithoutStart() {
            assertError(2, "@endfishbone without @startfishbone", "Effect\n@endfishbone\n");
        }

        @Test
        void nestedStartMarker() {
            assertError(3, "inside the diagram opened at line 1", "@startfishbone\nEffect\n@startfishbone\n");
        }

        @Test
        void unclosedStartMarker() {
            assertError(2, "never closed", "x\n@startfishbone\nEffect\n");
        }

        private static void assertError(final int line, final String messagePart, final String source) {
            final ParseException error = assertThrows(ParseException.class, () -> FishboneParser.parse(source));
            assertEquals(line, error.line(), error.getMessage());
            if (!error.getMessage().contains(messagePart)) {
                throw new AssertionError("Expected message containing '" + messagePart + "' but was: " + error.getMessage());
            }
        }
    }

    private static FishboneNode parseOne(final String source) {
        final List<FishboneNode> diagrams = FishboneParser.parse(source);
        assertEquals(1, diagrams.size());
        return diagrams.getFirst();
    }

    /** Renders a tree back as a 2-space outline, so expectations read like the source. */
    private static String outline(final FishboneNode node) {
        final StringBuilder out = new StringBuilder();
        outline(node, 0, out);
        return out.toString();
    }

    private static void outline(final FishboneNode node, final int depth, final StringBuilder out) {
        out.append("  ".repeat(depth)).append(node.label()).append('\n');
        node.children().forEach(child -> outline(child, depth + 1, out));
    }
}
