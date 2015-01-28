package fr.inria.diversify.analyzerPlugin.model.io;

import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.LoadingException;
import fr.inria.diversify.analyzerPlugin.model.TestRepresentation;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.util.Log;
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
    static private String NEW_TEST = "TB";
    static private String END_TEST = "TE";
    static private String ASSERT = "SA";
    static private String ASSERT_COUNT = "ASC";
    static private String TP_COUNT = "TPC";

    private class EntryLog implements Comparable {

        HashMap<Integer, String> idMap;

        public String fileName;

        public long millis;

        //Position or position id
        public String position;

        //executions of the loggin element
        int executions = 1;

        //Type of the logging element
        public String type;

        //Number of the line in the text file where this entry was located
        public int iteration = 0;

        //Min depth of the entry (Transplant points only)
        public int minDepth;

        public int maxDepth;

        public int meanDepth;

        public EntryLog(String file, int line, HashMap<Integer, String> idMap) {
            this.iteration = line;
            fileName = file;
            this.idMap = idMap;
        }


        @Override
        public int compareTo(Object o) {
            EntryLog e = (EntryLog) o;
            int result = (int) (millis - e.millis);
            return result == 0 ? iteration - e.iteration : result;
        }

        @Override
        public String toString() {
            return type + "," + position + "," + millis;
        }

        public void fromLineData(String[] lineData) {
            type = lineData[0];
            if (type.equals(END_TEST)) {
                millis = Long.parseLong(lineData[1]);
                return;
            }
            String p = idMap.get(Integer.parseInt(lineData[1]));
            position = p.substring(p.indexOf(">") + 1);
            if (type.equals(NEW_TEST) || type.equals(ASSERT)) {
                millis = Long.parseLong(lineData[2]);

            } else if (type.equals(TP_COUNT) || type.equals(ASSERT_COUNT)) {
                millis = Long.parseLong(lineData[2]);
                executions = Integer.parseInt(lineData[3]);
                if (type.equals(TP_COUNT)) {
                    minDepth = Integer.parseInt(lineData[4]);
                    meanDepth = Integer.parseInt(lineData[5]);
                    maxDepth = Integer.parseInt(lineData[6]);
                }
            } else if (type.equals(TP)) {
                millis = Long.parseLong(lineData[2]);
                maxDepth = meanDepth = minDepth = Integer.parseInt(lineData[3]);
            }
        }
    }


    private ArrayList<Exception> errors;

    private HashSet<TestRepresentation> declaredTest;

    HashMap<String, TransformationInfo> representations;


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
    public Collection<TransformationInfo> getRepresentations() {
        return representations.values();
    }

    private JSONObject readJSONFromFile(BufferedReader reader) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject result = new JSONObject();

        try {
            sourceJSONArray = new JSONArray(sb.toString());
            result.put("transformations", sourceJSONArray);
        } catch (JSONException e) {
            result = new JSONObject(sb.toString());
            sourceJSONArray = result.getJSONArray("transformations");
        }
        return result;
    }

    /**
     * Reads a sosie pool JSON file and converts it to data the plugin can easily understand
     *
     * @param path
     */
    public Collection<TransformationInfo> fromJSON(String path) throws IOException, JSONException {

        JSONObject jsonObject = readJSONFromFile(new BufferedReader(new FileReader(path)));
        JSONArray transformations = jsonObject.getJSONArray("transformations");

        JSONObject differences = null;
        if (jsonObject.has("differences")) {
            differences = jsonObject.getJSONObject("differences");
        }

        JSONObject tags;
        if (jsonObject.has("tags")) {
            tags = jsonObject.getJSONObject("tags");
        } else {
            tags = new JSONObject();
        }

        errors.clear();

        //Transformations points indexed by position
        representations = new HashMap<String, TransformationInfo>();

        totalTransformations = 0;
        totalPots = 0;

        for (int i = 0; i < transformations.length(); i++) {
            try {
                JSONObject jt = transformations.getJSONObject(i);
                String pos = jt.getJSONObject("transplantationPoint").getString("position");
                Integer index = jt.getInt("tindex");

                if (representations.containsKey(pos)) {
                    representations.get(pos).appendTransplant(jt, tags);
                    totalTransformations++;
                } else {
                    TransformationInfo tr = new TransformationInfo();
                    tr.fromJSONObject(jt, tags);
                    if (jt.has("nbVar")) tr.setVarDiff(jt.getInt("nbVar"));
                    if (jt.has("nbCall")) tr.setCallDiff(jt.getInt("nbCall"));
                    representations.put(pos, tr);
                    if (differences != null) {
                        if (differences.has(pos)) {
                            String s = differences.getString(pos);
                            tr.setDiffReport(s);
                        } else if (jt.has("diffString")) {
                            tr.setDiffReport(differences.getString(jt.getString("diffString")));
                        }
                    }
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
    public Collection<TransformationInfo> fromLogDir(String logDir) throws LoadingException {

        /*
        final String TESTS = "TEST";
        final String ASSERTS = "ASSERT";
        final String POT = "POT";
        */

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


        HashMap<Integer, String> idMap = new HashMap<Integer, String>();

        //Read the id file
        String fileName = "augmentedMetrics.id";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logDir + File.separator + fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                iteration++;
                String[] ln = line.split(" ");
                idMap.put(Integer.parseInt(ln[0]), ln[1]);
                //TODO: obtain this in static analysis store in properties file
                //TODO: now they go:
                /*
                if ( ln.length > 2 ) {
                    if (ln[2].equals(TESTS)) testDeclaredCount++;
                    if (ln[2].equals(ASSERTS)) assertionsDeclared++;
                }
                if ( ln[2].equals(POT) ) totalPotsIdFound++;*/
            }
        } catch (Exception e) {
            throw new LoadingException(iteration, fileName, e);
        }

        HashSet<TransformationInfo> tcpThisTest = new HashSet<TransformationInfo>(); //Transplant point reached in this test
        HashMap<String, AssertInfo> assertsThisTest = new HashMap<String, AssertInfo>();

        //Code positions not having a parent Test.
        ArrayList<EntryLog> entries = new ArrayList<EntryLog>();

        //Collect all the entry logs in different files to sort them by execution time
        for (File f : new File(logDir).listFiles()) {
            if (f.getName().startsWith("log")) {
                try {
                    fileName = f.getName();
                    iteration = 0;

                    BufferedReader logReader = new BufferedReader(new FileReader(f));
                    String l;
                    while ((l = logReader.readLine()) != null) {
                        iteration++;
                        String[] lineData;
                        lineData = l.split(";");
                        if (lineData.length > 0) {
                            EntryLog e = new EntryLog(f.getName(), iteration, idMap);
                            e.fromLineData(lineData);
                            entries.add(e);
                        }
                    }
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    errors.add(new LoadingException(iteration, fileName, e));

                }
            }
        }

        //Sort them by registration moment
        Collections.sort(entries);
        TestRepresentation currentTest = null;
        iteration = 0;

        //Move entries to elements
        for (EntryLog el : entries) {
            iteration++;
            try {
                if (el.type.equals(NEW_TEST)) {
                    currentTest = new TestRepresentation();
                    currentTest.setPosition(el.position);
                    currentTest.setRegisterTime(el.millis);
                    testExecutedCount++; //Count total test executions
                    if (tcpThisTest.size() > 0) {
                        testExecutedCoveringATPCount++;
                        //Count declared test covering at least a TP
                        if (!coveringTests.contains(currentTest.toString())) coveringTests.add(currentTest.toString());
                        //Count total assertions declared covering a test
                        for (String ar : assertsThisTest.keySet()) {
                            if (!coveringAsserts.contains(ar.toString())) coveringAsserts.add(ar.toString());
                        }
                    }

                    if (!declaredTest.contains(currentTest)) declaredTest.add(currentTest);
                    tcpThisTest.clear();
                    assertsThisTest.clear();
                } else {
                    if (currentTest == null) continue; //Ignore elements outside a test
                    if (el.type.equals(TP)) {
                        TestRepresentation test = currentTest;
                        //Obtain the TP by its position
                        //Integer index = Integer.parseInt(idMap.get(Integer.parseInt(el.position)));
                        TransformationInfo r = representations.get(el.position);
                        r.incHits(1);
                        totalPotsHitsCount++;
                        if (!tcpThisTest.contains(r)) tcpThisTest.add(r);

                        if (test != null) {
                            //Counts the test hit
                            r.addTestHit(test, 1);
                            //r.setDepth(test, el.maxDepth, el.stackMaxDepth);
                            r.setDepth(test, el.maxDepth, -1);
                        }
                    } else if (el.type.equals(ASSERT)) {
                        assertionsExecutedCount++;
                        AssertInfo ar = new AssertInfo(el.position);
                        assertsThisTest.put(el.position, ar);
                        currentTest.getAsserts().add(ar);
                        //Include this assert in the asserts of all TP in the log
                        for (TransformationInfo t : tcpThisTest) {
                            t.addAssertHit(ar, 1);
                        }

                        //Count asserts covering at least one TP
                        if (tcpThisTest.size() > 0) {
                            assertionsExecutedCoveringCount++;
                        }

                    } else if (el.type.equals(TP_COUNT)) {
                        //Integer index = Integer.parseInt(idMap.get(Integer.parseInt(el.position)));
                        TransformationInfo r = representations.get(el.position);
                        int k = el.executions;
                        totalPotsHitsCount += k;
                        r.incHits(k);
                        r.updateDepth(currentTest, el.minDepth, el.meanDepth, el.maxDepth, -1, -1, -1);
                    } else if (el.type.equals(ASSERT_COUNT)) {
                        AssertInfo ar = assertsThisTest.get(el.position);
                        int hits = el.executions;
                        assertionsExecutedCount += hits;

                        //Count asserts covering at least one TP
                        if (tcpThisTest.size() > 0) {
                            assertionsExecutedCoveringCount += hits;
                        }

                        for (TransformationInfo t : tcpThisTest) {
                            //Don't add asserts hits to TP that don't have them
                            if (t.getAssertHits(ar) > 0) {
                                t.addAssertHit(ar, hits - 1);
                            }
                        }
                    } else if (el.type.equals(END_TEST)) {
                        currentTest.setEndTime(el.millis);
                    }
                }
            } catch (Exception e) {
                errors.add(e);
            }
        }

        for (Exception e : errors) {
            Log.warn(e.getMessage());
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

    /**
     * Reads a sosie pool JSON file and a log file set and converts it to data the plugin can easily understand
     *
     * @param jsonPath Multisosie or sosie pool json file containing the transformations
     * @param logDir   Directory containing the
     */
    public Collection<TransformationInfo> fromScattered(
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
