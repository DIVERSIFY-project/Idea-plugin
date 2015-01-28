package fr.inria.diversify.analyzerPlugin.it;

import fr.inria.diversify.analyzerPlugin.ClassificationReport;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 23/10/2014.
 */
public class ClassificationReportTest {

    @Test
    @Ignore
    public void testDoReport() throws IOException, JSONException {
        String s = "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\single-sosies-pools\\ISSTA";
        ClassificationReport report = new ClassificationReport();
        report.createReport(s);
        assertEquals(true, new File(s + "\\report.html").exists());
    }
}
