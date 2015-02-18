package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.*;
import fr.inria.diversify.persistence.PersistenceException;
import fr.inria.diversify.persistence.json.output.JsonSectionOutput;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom section to output extended coverage
 *
 * Created by marodrig on 17/02/2015.
 */
public class JsonCoverageOutput extends JsonSectionOutput {

    public static final String COVERAGE = "coverage";
    public static final String TESTS = "tests";
    public static final String PER_TEST_COV = "perTestCoverage";
    public static final String ASSERTS = "asserts";
    public static final String HITS = "hits";
    public static final String MAX_DEPTH = "maxDepth";
    public static final String MIN_DEPTH = "minDepth";
    public static final String MEAN_DEPTH = "meanDepth";
    public static final String TEST_INDEX = "testIndex";

    private Collection<TransformationInfo> infos;

    public JsonCoverageOutput(Collection<TransformationInfo> infos) {
        this.infos = infos;
    }

    @Override
    public void write(JSONObject outputObject) {
        super.write(outputObject);

        JSONObject sectionObject = new JSONObject();
        try {
            outputObject.put(COVERAGE, sectionObject);

            //Test object
            JSONObject allTestsJson = new JSONObject();
            sectionObject.put(TESTS, allTestsJson);

            //Per test coverage
            JSONArray perTestArray = new JSONArray();
            sectionObject.put(PER_TEST_COV, perTestArray);

            HashMap<TestInfo, Integer> testSet = new HashMap<>();

            Integer testKey = 0;

            for (TransformationInfo info : infos) {

                for (Map.Entry<TestInfo, PertTestCoverageData> entry : info.getTests().entrySet()) {

                    TestInfo t = entry.getKey();

                    //Collect test
                    if ( !testSet.containsKey(t) ) {
                        JSONObject testJSON = new JSONObject();
                        testJSON.put(JsonSectionOutput.POSITION, t.getPosition());
                        //Save asserts
                        JSONArray asserts = new JSONArray();
                        for ( AssertInfo a : t.getAsserts() ) {
                            asserts.put(a.getPosition());
                        }
                        testJSON.put(ASSERTS, asserts);
                        allTestsJson.put(testKey.toString(), testJSON);

                        testSet.put(t, testKey);
                        testKey++;
                    }

                    //Save the Per test coverage
                    PertTestCoverageData data = entry.getValue();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put(HITS, data.getHits());
                    dataJson.put(MAX_DEPTH, data.getMaxDepth());
                    dataJson.put(MIN_DEPTH, data.getMinDepth());
                    dataJson.put(MEAN_DEPTH, data.getMeanDepth());
                    dataJson.put(TEST_INDEX, testSet.get(data.getTest()));
                    JSONArray transplants = new JSONArray();
                    for (TransplantInfo transplantInfo : info.getTransplants()) {
                        transplants.put(transplantInfo.getTransformation().getIndex().toString());
                    }
                    dataJson.put(JsonSectionOutput.TRANSPLANT, transplants);
                    perTestArray.put(dataJson);
                }
            }

        } catch (JSONException e) {
            throw new PersistenceException(e);
        }

    }

    public Collection<TransformationInfo> getInfos() {
        return infos;
    }

    public void setInfos(Collection<TransformationInfo> infos) {
        this.infos = infos;
    }
}
