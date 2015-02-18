package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonClassificationOutput;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfos;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 17/02/2015.
 */
public class JsonClassificationOutputTest {

    /**
     * Test the normal output of the tags information
     */
    @Test
    public void testWriteTags() throws JSONException {

        ArrayList<TransformationInfo> ts = createInfos();
        ts.get(0).getTransplants().get(0).setTags("weak");
        ts.get(0).getTransplants().get(1).setTags("strong");
        ts.get(1).getTransplants().get(0).setTags("crazy");

        JSONObject out = new JSONObject();
        JsonClassificationOutput section = new JsonClassificationOutput(ts);
        section.write(out);

        assertTrue(out.has(JsonClassificationOutput.TAGS));

        JSONObject tags = out.getJSONObject(JsonClassificationOutput.TAGS);
        assertEquals("weak",   tags.getString(ts.get(0).getTransplants().get(0).getIndex().toString()));
        assertEquals("strong", tags.getString(ts.get(0).getTransplants().get(1).getIndex().toString()));
        assertEquals("crazy",  tags.getString(ts.get(1).getTransplants().get(0).getIndex().toString()));
    }

}
