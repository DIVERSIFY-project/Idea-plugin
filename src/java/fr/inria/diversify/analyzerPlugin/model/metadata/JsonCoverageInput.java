package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.Transformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static fr.inria.diversify.analyzerPlugin.model.metadata.JsonCoverageOutput.*;

/**
 * Reads the coverage information
 *
 * Created by marodrig on 17/02/2015.
 */
public class JsonCoverageInput extends JsonTestEyeSectionInput {

    @Override
    public void read(HashMap<UUID, Transformation> transformations) {
        super.read(transformations);

        try {
            if ( !getJsonObject().has(COVERAGE) ) return;

            JSONObject cov = getJsonObject().getJSONObject(COVERAGE);

            //Collect tests
            HashMap<Integer, TestInfo> tests = new HashMap<>();
            JSONObject allTestObj = cov.getJSONObject(TESTS);

            Iterator<String> keys = allTestObj.keys();
            while ( keys.hasNext() ) {
                String testIndex = (String) keys.next();
                JSONObject testObj = allTestObj.getJSONObject(testIndex);
                TestInfo t =  new TestInfo(testObj.getString(POSITION));
                JSONArray assertsArray = testObj.getJSONArray(ASSERTS);
                for ( int j = 0; j < assertsArray.length(); j++ ) {
                    t.getAsserts().add(new AssertInfo(assertsArray.getString(j)));
                }
                tests.put(Integer.parseInt(testIndex), t);
            }

            //Collect pertest information
            JSONArray perTest = cov.getJSONArray(PER_TEST_COV);
            for ( int i = 0; i < perTest.length(); i++ ) {
                JSONObject perObj = perTest.getJSONObject(i);
                int tesIndex = perObj.getInt(TEST_INDEX);
                PertTestCoverageData data = new PertTestCoverageData(tests.get(tesIndex));
                data.addHits(perObj.getInt(HITS));
                data.setMaxDepth(perObj.getInt(MAX_DEPTH));
                data.setMinDepth(perObj.getInt(MIN_DEPTH));
                data.setMeanDepth(perObj.getInt(MEAN_DEPTH));

                JSONArray transplants = perObj.getJSONArray(TRANSPLANT);
                for ( int j = 0; j < transplants.length(); j++ ) {
                    TransplantInfo ti = getTransplantInfos().get(UUID.fromString(transplants.getString(j)));
                    ti.getTransplantationPoint().getTests().put(data.getTest(), data);
                }
            }
        } catch (JSONException e) {
            throwError("Cannot read coverage information", e, false);
        }


    }

}
