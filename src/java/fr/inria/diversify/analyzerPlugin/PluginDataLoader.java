package fr.inria.diversify.analyzerPlugin;

import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class reads data in files an streams into the IntelliJIdea plugin internal data format
 * <p/>
 * Created by marodrig on 03/09/2014.
 */
public class PluginDataLoader {

    static private String TP = "S";

    static private String NEW_TEST = "NewTest";

    private ArrayList<Exception> errors;

    private HashSet<TestRepresentation> declaredTest;

    HashMap<String, TransformationRepresentation> representations;

    //The index belongs to the Transformation, we order the transformations by TP so there must be a place to index
    //wich TP belongs to an indexed transformation. Ugly, but I don't think of a better solution without a full
    //refactoring of the code
    HashMap<Integer, TransformationRepresentation> indexedRepresentations;

    private JSONArray sourceJSONArray;

    /**
     * Asserts registered in the intrumention process
     */
    private int assertionsDeclared;
    /**
     * Test registered in the instrumentation process
     */
    private int testDeclaredCount;

    /**
     * Counts the tests declared covering at least one TP
     */
    private int testDeclaredCoveringATPCount;

    /**
     * Total transformations in the JSON file
     */
    private int totalTransformations;
    /**
     * Total pots in the in the JSON file
     */
    private int totalPots;

    /**
     * Counts the assertion covering at least one TP
     */
    private int assertionsExecutedCoveringCount;

    /**
     * Count the assertions that where executed at leas once
     */
    private int asserstDeclaredCoveringATPCount;

    /**
     * Count the tests that where executed
     */
    private int testExecutedCount;

    /**
     * Counts the assertions that where executed
     */
    private int assertionsExecutedCount;

    /**
     * Brute hits on TP. I.E. the sum of all hits over all TPs
     */
    private long totalPotsHitsCount;
    private int testExecutedCoveringATPCount;


    public PluginDataLoader() {
        errors = new ArrayList<Exception>();
    }

    /**
     * Get las loaded TransformationRepresentations
     *
     * @return
     */
    public Collection<TransformationRepresentation> getRepresentations() {
        return representations.values();
    }

    private JSONArray readJSONFromFile(BufferedReader reader) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            sourceJSONArray = new JSONArray(sb.toString());
        } catch (JSONException e) {
            JSONObject object = new JSONObject(sb.toString());
            sourceJSONArray = object.getJSONArray("transformations");
        }
        return sourceJSONArray;
    }

    /**
     * Reads a sosie pool JSON file and converts it to data the plugin can easily understand
     *
     * @param path
     */
    public Collection<TransformationRepresentation> fromJSON(String path) throws IOException, JSONException {

        JSONArray jsonArray = readJSONFromFile(new BufferedReader(new FileReader(path)));

        errors.clear();

        //Transformations points indexed by position
        representations = new HashMap<String, TransformationRepresentation>();

        //Transformation points indexed by Transformation index
        indexedRepresentations = new HashMap<Integer, TransformationRepresentation>();

        totalTransformations = 0;
        totalPots = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jt = jsonArray.getJSONObject(i);
                String pos = jt.getJSONObject("transplantationPoint").getString("position");
                Integer index = jt.getInt("tindex");

                if (representations.containsKey(pos)) {
                    representations.get(pos).appendTransplant(jt);
                    indexedRepresentations.put(index, representations.get(pos));

                    totalTransformations++;
                } else {
                    TransformationRepresentation tr = new TransformationRepresentation();
                    tr.fromJSONObject(jt);
                    representations.put(pos, tr);
                    indexedRepresentations.put(index, tr);

                    //Count transformations and pots
                    totalPots++;
                    totalTransformations++;
                }
            } catch (JSONException e) {
                errors.add(e);
            }
        }

        return representations.values();
    }


    /**
     * Reads from a log dir when there is already property loaded
     *
     * @param logDir Dir of the log
     * @return The collection of TransformationRepresentation with the log information added
     */
    public Collection<TransformationRepresentation> fromLogDir(String logDir) throws LoadingException {

        final String TESTS = "TEST";
        final String ASSERTS = "ASSERT";
        final String POT = "POT";

        testDeclaredCount = 0;
        testDeclaredCoveringATPCount = 0;

        totalPotsHitsCount = 0;

        testExecutedCount = 0;
        testExecutedCoveringATPCount = 0;

        assertionsExecutedCount = 0;
        assertionsExecutedCoveringCount = 0;

        assertionsDeclared = 0;
        asserstDeclaredCoveringATPCount = 0;

        //HashSet<String> assertsExecuted = new HashSet<String>();
        HashSet<String> coveringAsserts = new HashSet<String>();
        HashSet<String> coveringTests = new HashSet<String>();
        declaredTest = new HashSet<TestRepresentation>();

        //To collect information regarding the errors
        int iteration = 0;
        String fileName = "";

        HashMap<Integer, String> idMap = new HashMap<Integer, String>();

        //Read the id file
        try {
            fileName = "id";
            BufferedReader reader = new BufferedReader(new FileReader(logDir + File.separator + "id"));
            String line;
            while ((line = reader.readLine()) != null) {
                iteration++;
                String[] ln = line.split(" ");
                idMap.put(Integer.parseInt(ln[0]), ln[1]);
                if (ln[2].equals(TESTS)) testDeclaredCount++;
                if (ln[2].equals(ASSERTS)) assertionsDeclared++;
                //if ( ln[2].equals(POT) ) totalPotsIdFound++;
            }
        } catch (Exception e) {
            throw new LoadingException(iteration, fileName, e);
        }

        HashSet<TransformationRepresentation> tcpThisTest = new HashSet<TransformationRepresentation>(); //Transplant point reached in this test
        HashMap<String, AssertRepresentation> assertsThisTest = new HashMap<String, AssertRepresentation>();

        //Code positions not having a parent Test.
        ArrayList<CodePosition> orphans = new ArrayList<CodePosition>();

        for (File f : new File(logDir).listFiles()) {
            if (f.getName().startsWith("log")) {
                try {
                    fileName = f.getName();
                    iteration = 0;

                    BufferedReader logReader = new BufferedReader(new FileReader(f));
                    String l;
                    TestRepresentation currentTest = null;

                    while ((l = logReader.readLine()) != null) {
                        iteration++;
                        String[] lineData;
                        if (l.endsWith("$$$")) {
                            lineData = l.substring(0, l.length() - 3).split(";");
                        } else {
                            lineData = l.split(";");
                        }

                        if (lineData[0].equals(NEW_TEST)) {
                            currentTest = new TestRepresentation();
                            currentTest.fromLogString(l);

                            if (lineData.length > 2) {
                                currentTest.setRegisterTime(Integer.parseInt(lineData[2]));
                            }

                            testExecutedCount++; //Count total test executions
                            if (tcpThisTest.size() > 0) {
                                testExecutedCoveringATPCount++;
                                if (!coveringTests.contains(currentTest.toString())) {
                                    //Count declared test covering at least a TP
                                    coveringTests.add(currentTest.toString());
                                }
                                for (String ar : assertsThisTest.keySet()) {
                                    //Count total assertions declared covering a test
                                    if (!coveringAsserts.contains(ar.toString())) {
                                        coveringAsserts.add(ar.toString());
                                    }
                                }
                            }

                            if (!declaredTest.contains(currentTest)) {
                                declaredTest.add(currentTest);
                            }

                            tcpThisTest.clear();
                            assertsThisTest.clear();
                        } else {
                            if (lineData[0].equals(TP)) {
                                TestRepresentation test = currentTest;
                                if (test == null) {
                                    //Find the test by time of execution
                                    test = findTest(lineData);
                                }

                                //Obtain the TP by its position
                                Integer index = Integer.parseInt(idMap.get(Integer.parseInt(lineData[1])));
                                TransformationRepresentation r = indexedRepresentations.get(index);
                                r.incHits(1);
                                totalPotsHitsCount++;
                                if (!tcpThisTest.contains(r)) {
                                    tcpThisTest.add(r);
                                }

                                if (test != null) {
                                    //Counts the test hit
                                    r.addTestHit(test, 1);
                                } else {
                                    orphans.add(r);
                                }
                            } else if (lineData[0].equals("SA")) {
                                assertionsExecutedCount++;

                                String pos = idMap.get(Integer.parseInt(lineData[1]));

                                AssertRepresentation ar = new AssertRepresentation(pos);
                                assertsThisTest.put(pos, ar);
                                currentTest.getAsserts().add(ar);
                                //Include this assert in the asserts of all TP in the log
                                for (TransformationRepresentation t : tcpThisTest) {
                                    t.addAssertHit(ar, 1);
                                }

                                //Count asserts covering at least one TP
                                if (tcpThisTest.size() > 0) {
                                    assertionsExecutedCoveringCount++;
                                }

                            } else if (lineData[0].equals("TPC")) {
                                Integer index = Integer.parseInt(idMap.get(Integer.parseInt(lineData[1])));
                                TransformationRepresentation r = indexedRepresentations.get(index);
                                int k = Integer.parseInt(lineData[2]);
                                totalPotsHitsCount += k;
                                r.incHits(k);
                            } else if (lineData[0].equals("ASC")) {
                                String pos = idMap.get(Integer.parseInt(lineData[1]));
                                AssertRepresentation ar = assertsThisTest.get(pos);
                                int hits = Integer.parseInt(lineData[2]);

                                assertionsExecutedCount += hits;

                                //Count asserts covering at least one TP
                                if (tcpThisTest.size() > 0) {
                                    assertionsExecutedCoveringCount += hits;
                                }

                                for (TransformationRepresentation t : tcpThisTest) {
                                    //Don't add asserts hits to TP that don't have them
                                    if (t.getAssertHits(ar) > 0) {
                                        t.addAssertHit(ar, hits - 1);
                                    }
                                }
                            } else if (lineData[0].equals("TE")) {
                                currentTest.setEndTime(Integer.parseInt(lineData[1]));
                            }
                        }
                    }
                } catch (Exception e) {
                    errors.add(new LoadingException(iteration, fileName, e));
                }
            }
        }

        //testDeclaredCount
        testDeclaredCoveringATPCount = coveringTests.size();
        //testExecutedCount


        //assertionsDeclared
        asserstDeclaredCoveringATPCount = coveringAsserts.size();

        //assertionsExecutedCount
        //assertionsExecutedCoveringCount

        return representations.values();
    }

    private TestRepresentation findTest(String[] lineData) {
        return null;
    }

    private void registerSimpleAssertion() {

    }

    /**
     * Reads a sosie pool JSON file and a log file set and converts it to data the plugin can easily understand
     *
     * @param jsonPath Multisosie or sosie pool json file containing the transformations
     * @param logDir   Directory containing the
     */
    public Collection<TransformationRepresentation> fromScattered(
            String jsonPath, String logDir) throws LoadingException {

        errors.clear();

        try {
            fromJSON(jsonPath);
        } catch (Exception e) {
            throw new LoadingException(0, jsonPath, e);
        }

        return fromLogDir(logDir);
    }

    /**
     * Test declared in the code
     *
     * @return Number of method test
     */
    public int getTestDeclaredCount() {
        return testDeclaredCount;
    }

    /**
     * Number of times all method test gets executed. A method test can execute more than once.
     *
     * @return
     */
    public int getTestExecutedCount() {
        return testExecutedCount;
    }

    /**
     * Number of asserts that executes at least once
     *
     * @return
     */
    public int getAssertsDeclaredCoveringATP() {
        return asserstDeclaredCoveringATPCount;
    }

    /**
     * Errors obtained during the last load.
     *
     * @return
     */
    public ArrayList<Exception> getErrors() {
        return errors;
    }

    public JSONArray getSourceJSONArray() {
        return sourceJSONArray;
    }


    public int getAssertionsDeclared() {
        return assertionsDeclared;
    }

    public long getPotsTotalHitCount() {
        return totalPotsHitsCount;
    }

    public int getTotalPots() {
        return totalPots;
    }


    public int getTotalTransformations() {
        return totalTransformations;
    }

    public int getAssertionsExecutedCoveringCount() {
        return assertionsExecutedCoveringCount;
    }

    public int getTestDeclaredCoveringATPCount() {
        return testDeclaredCoveringATPCount;
    }


    /**
     * All executed test in the program
     */
    public HashSet<TestRepresentation> getDeclaredTest() {
        return declaredTest;
    }

    public int getAssertionsExecutedCount() {
        return assertionsExecutedCount;
    }

    public int getTestExecutedCoveringATPCount() {
        return testExecutedCoveringATPCount;
    }
}
