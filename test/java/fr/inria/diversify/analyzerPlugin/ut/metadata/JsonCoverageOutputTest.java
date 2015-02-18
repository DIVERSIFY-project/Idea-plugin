package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.metadata.JsonCoverageOutput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.ut.MockInputProgram;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfosWithCoverage;
import static fr.inria.diversify.analyzerPlugin.model.metadata.JsonCoverageOutput.*;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 17/02/2015.
 */
public class JsonCoverageOutputTest {

    /**
     * Test the normal output of the coverage information
     */
    @Test
    public void testWriteCoverage() throws JSONException {
        List<Transformation> ts = createTransformations(new MockInputProgram());
        JsonCoverageOutput outSection = new JsonCoverageOutput(createInfosWithCoverage(ts));
        JSONObject out = new JSONObject();
        outSection.write(out);

        assertTrue(out.has(COVERAGE));

        JSONObject cov = out.getJSONObject(COVERAGE);
        assertTrue(cov.has(TESTS));
        assertTrue(cov.has(PER_TEST_COV));

        assertEquals(2, cov.getJSONObject(TESTS).length());
        assertEquals(3, cov.getJSONArray(PER_TEST_COV).length());

        assertGoodTestWriting(cov, 0, "org.MyClassTest:10", 2, "org.MyClassTest:20");
        assertGoodTestWriting(cov, 1, "org.MyOtherClassTest:100", 1, "org.MyClassTest:105");

        assertGoodPerTestWriting(cov, 0, 0, 1, 3);
        assertGoodPerTestWriting(cov, 1, 1, 5, 3);
        assertGoodPerTestWriting(cov, 2, 1, 3, 2);
    }

    private void assertGoodPerTestWriting(JSONObject cov, int index, int testIndex, int hits, int depth) throws JSONException {

        JSONObject per = cov.getJSONArray(PER_TEST_COV).getJSONObject(index);
        assertEquals(hits, per.getInt(HITS));
        assertEquals(depth, per.getInt(MEAN_DEPTH));
        assertEquals(testIndex, per.getInt(TEST_INDEX));

    }


    private void assertGoodTestWriting(JSONObject t, Integer id,
                                       String position, int assertCount, String assert1Position) throws JSONException {
        JSONObject test = t.getJSONObject(TESTS);
        String i = id.toString();
        assertEquals(position, test.getJSONObject(i).getString(POSITION));
        assertEquals(assertCount, test.getJSONObject(i).getJSONArray(ASSERTS).length());
        assertEquals(assert1Position, test.getJSONObject(i).getJSONArray(ASSERTS).getString(0));
    }

}
