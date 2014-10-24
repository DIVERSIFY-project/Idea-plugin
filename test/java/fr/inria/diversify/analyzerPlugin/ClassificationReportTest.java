package fr.inria.diversify.analyzerPlugin;

import org.json.JSONException;
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
    public void testDoReport() throws IOException, JSONException {
        String s = "C:\\MarcelStuff\\projects\\DIVERSE\\programs\\single-sosies-pools\\NonRep-Indexed";
        ClassificationReport report = new ClassificationReport();
        report.createReport(s);
        assertEquals(true, new File(s + "\\report.html").exists());
    }
}
