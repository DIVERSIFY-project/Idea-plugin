package fr.inria.diversify.analyzerPlugin.model.io;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import org.junit.Test;

import java.io.StringWriter;
import java.util.UUID;

import static org.junit.Assert.*;

public class TransformationLatexExporterTest {

    @Test
    public void testExportReplace() throws Exception {
        TransplantInfo tInfo = new TransplantInfo();
        tInfo.setPosition("com.otherpos.Class2:2");
        tInfo.setSource("return 2");
        tInfo.setType("replace");

        TransformationInfo tpInfo = new TransformationInfo();
        tpInfo.setPosition("com.position.Class1:1");
        tpInfo.setSource("return 1");

        tpInfo.getTransplants().add(tInfo);
        tInfo.setTransplantationPoint(tpInfo);
        tInfo.setIndex(UUID.fromString("1cc5c5d4-433c-11e5-92d7-aba9383cc041"));

        StringWriter writer = new StringWriter();

        TransplantLatexExporter exporter = new TransplantLatexExporter();
        exporter.setProjectName("Example");
        exporter.export(writer, tInfo, "Some comments here", "id");

        exporter.export("text.tex", tInfo, "Some comments here", "id");

        String result = writer.toString();
        assertTrue(result.contains(tInfo.getPosition()));
        assertTrue(result.contains(tpInfo.getPosition()));
        assertTrue(result.contains(tInfo.getSource()));
        assertTrue(result.contains(tpInfo.getSource()));

    }
}