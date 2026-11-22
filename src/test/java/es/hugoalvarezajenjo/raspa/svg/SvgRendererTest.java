package es.hugoalvarezajenjo.raspa.svg;

import es.hugoalvarezajenjo.raspa.Raspa;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SvgRendererTest {

    @Test
    void producesAWellFormedSvgDocument() throws Exception {
        final Document svg = parseXml(Raspa.toSvg("Effect\n  Category\n    Cause\n").getFirst());
        final Element root = svg.getDocumentElement();

        assertEquals("svg", root.getTagName());
        assertEquals("http://www.w3.org/2000/svg", root.getAttribute("xmlns"));
        assertFalse(root.getAttribute("viewBox").isEmpty());
        assertEquals(3, svg.getElementsByTagName("line").getLength());
        assertEquals(3, svg.getElementsByTagName("text").getLength());
    }

    @Test
    void bonesAreStroked() throws Exception {
        final Document svg = parseXml(Raspa.toSvg("Effect\n  Category\n").getFirst());
        final Element group = (Element) svg.getElementsByTagName("line").item(0).getParentNode();

        assertFalse(group.getAttribute("stroke").isEmpty());
    }

    @Test
    void escapesLabelText() throws Exception {
        final Document svg = parseXml(Raspa.toSvg("Q&A <fails>\n  R&D\n").getFirst());

        assertEquals("Q&A <fails>", svg.getElementsByTagName("text").item(0).getTextContent());
    }

    private static Document parseXml(final String xml) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }
}
