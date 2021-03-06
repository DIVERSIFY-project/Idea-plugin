package fr.inria.diversify.analyzerPlugin.model;

import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import fr.inria.diversify.util.Log;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents Transplantation point. Contains the data of the transformation needed for the plugin in a more
 * plugin friendly way
 * <p/>
 * Created by marodrig on 04/09/2014.
 */
public class TransformationInfo extends CodePosition {

    private int totalTestHits;

    private int appliedTransformIndex = -1;
    private int varDiff;
    private int callDiff;
    private List<String> logMessages;
    private double meanDepth;

    public long getTotalAssertionHits() {
        return totalAssertionHits;
    }

    public void setTotalAssertionHits(long totalAssertionHits) {
        this.totalAssertionHits = totalAssertionHits;
    }

    /**
     * Total hits of this TP
     */
    private long hits = 0;

    private long totalAssertionHits = 0;

    private String diffReport;

    /**
     * Increments the hits of the TP
     *
     * @param i number of increments
     */
    public void incHits(int i) {
        hits = hits + i;
    }

    public HashMap<TestInfo, PertTestCoverageData> getTests() {
        return tests;
    }

    public void setTest(HashMap<TestInfo, PertTestCoverageData> test) {
        this.tests = test;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    /**
     * Position that the storage says this transformation was.
     * This may vary when actually finding it in the program's code
     */
    private String storagePosition;

    /**
     * Source that the storage says this transformation has.
     * This may vary when actually finding it in the program's code
     */
    private String storageSource;

    private String spoonType;

    private String variableMapping;

    private List<TransplantInfo> transplants;

    //Data associated to the test
    private HashMap<TestInfo, PertTestCoverageData> tests;


    private String type;

    private HashMap<AssertInfo, Integer> assertCount;

    private HashMap<TestInfo, Integer> testCount;

    /*
    public TransformationRepresentation(String potPosition) {
        this.position = potPosition;
        transplants = new ArrayList<Transplant>();
    }*/

    /**
     * Extract a series of data out of the json object
     */
    public TransformationInfo() {
        transplants = new ArrayList<TransplantInfo>();
        tests = new HashMap<TestInfo, PertTestCoverageData>();
        assertCount = new HashMap<AssertInfo, Integer>();
        testCount = new HashMap<TestInfo, Integer>();
    }


    /**
     * Returns the number of times an instrumented test method executed and hit this TP
     *
     * @param rep Assert that we want to query for
     * @return
     */
    public int getTestHits(TestInfo rep) {
        return testCount.containsKey(rep) ? testCount.get(rep) : 0;
    }

    /**
     * Returns the number of times an assertions was executed after this TP was hit
     *
     * @param rep Assert that we want to query for
     * @return
     */
    public int getAssertHits(AssertInfo rep) {
        return assertCount.containsKey(rep) ? assertCount.get(rep) : 0;
    }

    /**
     * Add an assertion hits to the TP
     *
     * @param ar   assertion hit after the TP
     * @param hits number of hits
     */
    public void addAssertHit(AssertInfo ar, int hits) {
        addHits(assertCount, ar, hits);
        setTotalAssertionHits(getTotalAssertionHits() + hits);
    }

    /**
     * Sets the first depth found for this transformation
     */
    public void setDepth(TestInfo test, int depth, int stackDepth) {
        PertTestCoverageData perTest;
        if (!this.tests.containsKey(test)) {
            perTest = new PertTestCoverageData(test);
            tests.put(test, perTest);
        } else {
            perTest = this.tests.get(test);
        }
        perTest.setDepth(depth);
        perTest.setStackDepth(stackDepth);
    }

    /**
     * Sets all the depth values found for this transformation
     */
    public void updateDepth(TestInfo test, int minDepth, int meanDepth, int maxDepth,
                            int stackMin, int stackMean, int stackMax) {
        PertTestCoverageData perTest;
        if (!this.tests.containsKey(test)) {
            perTest = new PertTestCoverageData(test);
            tests.put(test, perTest);
        } else {
            perTest = this.tests.get(test);
        }
        perTest.setMinDepth(minDepth);
        perTest.setMeanDepth(meanDepth);
        perTest.setMaxDepth(maxDepth);
        perTest.setStackMinDepth(stackMin);
        perTest.setStackMeanDepth(stackMean);
        perTest.setStackMaxDepth(stackMax);
    }


    /**
     * Add an test hits to the TP
     *
     * @param test assertion hit after the TP
     * @param hits number of hits
     */
    public void addTestHit(TestInfo test, int hits) {
        PertTestCoverageData perTest;
        if (!this.tests.containsKey(test)) {
            perTest = new PertTestCoverageData(test);
            tests.put(test, perTest);
        } else {
            perTest = this.tests.get(test);
        }

        //Register the hits in this test
        perTest.addHits(hits);

        //Register the total hit count
        addHits(testCount, test, hits);
        setTotalTestHits(getTotalTestHits() + hits);
    }

    public void addHits(HashMap map, CodePosition pos, int hits) {
        if (map.containsKey(pos)) {
            int k = (Integer) map.get(pos);
            map.put(pos, hits + k);
        } else {
            map.put(pos, hits);
        }
    }

    /**
     * Returns all the assertions that hits this TP
     *
     * @return
     */
    public Collection<AssertInfo> getAsserts() {
        return assertCount.keySet();
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the position of the transplant point (pot)
     *
     * @return
     */
    public String getPosition() {
        return position;
    }

    public void setPosition(String value) {
        position = value;
    }


    /**
     * Convert the tags of all transplant into a JSON ArrayList.
     * This is used to store all tags that annotates the Transplants of this TP
     *
     * @param representations
     * @return a JSONObject ArrayList with the Transformations serialized
     */
    public static JSONObject tagsToJSON(Collection<TransformationInfo> representations) throws JSONException {

        JSONObject result = new JSONObject();
        for (TransformationInfo r : representations) {
            for (TransplantInfo t : r.getTransplants()) {
                if (!(t.getTags() == null || t.getTags().isEmpty())) {
                    result.put(String.valueOf(t.getIndex()), t.getTags());
                }
            }
        }
        return result;
    }

    public String getSpoonType() {
        return spoonType;
    }

    public void setSpoonType(String spoonType) {
        this.spoonType = spoonType;
    }

    public String getVariableMapping() {
        return variableMapping;
    }

    public void setVariableMapping(String variableMapping) {
        this.variableMapping = variableMapping;
    }

    public List<TransplantInfo> getTransplants() {
        return transplants;
    }

    public void setTransplants(List<TransplantInfo> transplantPositions) {
        this.transplants = transplantPositions;
        for (TransplantInfo t : transplantPositions) {
            t.setTransplantationPoint(this);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getPosition();
    }

    public int getTotalTestHits() {
        return totalTestHits;
    }

    public void setTotalTestHits(int totalTestHits) {
        this.totalTestHits = totalTestHits;
    }

    /**
     * Copy a backup of the source if none exist
     *
     * @param destDir Destination dir where the transformation code is going to be stored
     */
    private void makeOriginalSourceBackUp(ASTTransformation transf, String srcDir, String destDir) throws IOException {
        //1. Copy a backup of the source if none exist
        String filePath = transf.getTransplantationPoint().getSourcePackage().getQualifiedName().replaceAll("\\.", "/");

        //Create destination dir
        File f = new File(destDir + File.separator + filePath);
        if (!f.exists()) {
            f.mkdirs();
        }

        filePath = transf.getTransplantationPoint().getSourceClass().getQualifiedName().replaceAll("\\.", "/");
        String sourcePath = srcDir + File.separator + filePath + "." + "java";
        filePath = destDir + File.separator + filePath + "." + "java.backup";

        f = new File(filePath);
        if (!f.exists()) {
            FileUtils.copyFile(new File(sourcePath), f, false);
        }
    }

    /**
     * Copy the modified source code back to the src
     *
     * @param modDir Modified source code
     * @param srcDir Original source
     */
    private void copyModifiedSource(ASTTransformation transf, String modDir, String srcDir) throws IOException {
        String filePath = transf.getTransplantationPoint().getSourceClass().getQualifiedName().replaceAll("\\.", "/");
        String sourcePath = srcDir + File.separator + filePath + "." + "java";
        filePath = modDir + File.separator + filePath + "." + "java";
        FileUtils.copyFile(new File(sourcePath), new File(filePath));
    }


    /**
     * Restores the source file to the source directory
     *
     * @param transf  Transplant to be restored
     * @param srcDir  Source dir dir where the transformation code is going to be restored
     * @param destDir Destination dir where the original code is
     */
    private void restoreTransformation(ASTTransformation transf, String srcDir, String destDir) throws IOException {

        String filePath = transf.getTransplantationPoint().getSourceClass().getQualifiedName().replaceAll("\\.", "/");
        String sourcePath = srcDir + File.separator + filePath + ".java";
        filePath = destDir + File.separator + filePath + "." + "java.backup";
        try {
            transf.restore(destDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        FileUtils.copyFile(new File(filePath), new File(sourcePath));
    }

    public boolean isTransplantApplied(TransplantInfo transplant) {
        return appliedTransformIndex == transplants.indexOf(transplant);
    }

    /**
     * Switch back and forth from the original code to the transformed code
     *
     * @param srcDir  Source code to be transformed
     * @param destDir Destination dir where the transformation code is going to be stored
     * @throws Exception
     */
    public void switchTransformation(TransplantInfo transplant, String srcDir, String destDir) throws Exception {

        //Restore all applied transplants
        for ( TransplantInfo t : transplants ) {
            if ( t.isApplied() ) {
                restoreTransformation((ASTTransformation) t.getTransformation(), srcDir, destDir);
                t.setTransformationApplied(false);
            }
        }

        int index = transplants.indexOf(transplant);
        if (index == -1) throw new IndexOutOfBoundsException("This transplant does not belogs to the TP");

        //If the transplant was not applied, it means that the user wants to apply
        if ( index != appliedTransformIndex) {
            //We work only with ASTTransformations
            ASTTransformation transf = (ASTTransformation)transplant.getTransformation();

            appliedTransformIndex = index;
            //Make a backup of the source
            makeOriginalSourceBackUp(transf, srcDir, destDir);

            //Delete if the transformed file exists to avoid rewriting exceptions on the "apply" method
            File destFile = new File(destDir);
            if ( destFile.exists() ) destFile.delete();

            //Apply transformation
            transf.apply(destDir);

            //Copy the modified source back to production
            copyModifiedSource(transf, srcDir, destDir);
        } else {
            appliedTransformIndex = -1;
        }
    }


    /**
     * Restore transformations in Transplantation point class
     */
    private void restoreTransformationInTPClass() {
        //TransformationRepresentation rep = getParentRepresentation();
    }

    /**
     * Returns the applied transplant over the transplantation point
     *
     * @return A Transplant or null of none applied
     */
    public TransplantInfo getAppliedTransplant() {
        if (appliedTransformIndex == -1) return null;
        return transplants.get(appliedTransformIndex);
    }

    public boolean hasVisibleTransplants() {
        for (TransplantInfo t : transplants) {
            if (t.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public String getDiffReport() {
        return diffReport;
    }

    public void setDiffReport(String diffReport) {
        StringBuilder sb = new StringBuilder(diffReport);
        this.diffReport = sb.toString();
        Log.info("Diff at " + this.getPosition() + " set to " + diffReport);
    }

    public int getVarDiff() {
        return varDiff;
    }

    public int getCallDiff() {
        return callDiff;
    }

    public void setVarDiff(int varDiff) {
        this.varDiff = varDiff;
    }

    public void setCallDiff(int callDiff) {
        this.callDiff = callDiff;
    }

    /**
     * Initializes the representation from the JSON object
     */
    @Deprecated
    public void fromJSONObject(JSONObject object, JSONObject tags) throws JSONException {
        JSONObject tp = object.getJSONObject("transplantationPoint");
        if (tp.has("sourceCode")) setSource(tp.getString("sourceCode"));
        else setSource(tp.getString("sourcecode"));
        setPosition(tp.getString("position"));
        setSpoonType(tp.getString("type"));
        setType(object.getString("name"));
        appendTransplant(object, tags);
    }

    /**
     * Initializes the info from the Transformation object
     *
     * @param t Transformation object
     */
    public void fromTransformation(ASTTransformation t) {
        setSource(t.getTransplantationPoint().codeFragmentString());
        setPosition(t.getTransplantationPoint().positionString());
        setSpoonType(t.getTransplantationPoint().getCodeFragmentTypeSimpleName());
        setType(t.getName());
        appendTransplant(t);
    }

    /**
     * Extract the transplant out of the JSON object and appends it to the list of TP over this point
     *
     * @param jt   JSON with the transformation
     * @param tags Array with the tags to every transformation
     */
    @Deprecated
    public void appendTransplant(JSONObject jt, JSONObject tags) throws JSONException {
        if (jt.has("transplant")) {
            JSONObject tp = jt.getJSONObject("transplant");
            TransplantInfo t = new TransplantInfo();
            t.setPosition(tp.getString("position"));
            if (tp.has("sourceCode")) t.setSource(tp.getString("sourceCode"));
            else t.setSource(tp.getString("sourcecode"));
            t.setSpoonType(tp.getString("type"));
            //t.setIndex(jt.getInt("tindex"));
            t.setType(jt.getString("name"));
            String sIndex = String.valueOf(t.getIndex());
            if (tags.has(sIndex)) {
                t.setTags(tags.getString(sIndex));
            }
            if (jt.has("variableMapping")) {
                t.setVariableMap(jt.getJSONObject("variableMapping").toString());
            }
            t.setTransplantationPoint(this);
            transplants.add(t);
        } else {
            TransplantInfo delete = new TransplantInfo();
            delete.setType("delete");
            //delete.setIndex(jt.getInt("tindex"));
            delete.setTransplantationPoint(this);
            transplants.add(delete);
        }
    }

    /**
     * Create a string out of a variable map
     *
     * @param v
     * @return
     */
    private String getVariableMapStr(Map<String, String> v) {
        StringBuilder sb = new StringBuilder("[");
        if (v != null)
            for (Map.Entry<String, String> k : v.entrySet())
                sb.append("; " + k.getKey() + "->" + k.getValue());
        sb.append("]");
        return sb.toString();
    }

    /**
     * Appends transplant from a code fragment
     *
     * @param t Transformation containing the transplant
     */
    public void appendTransplant(ASTTransformation t) {
        CodeFragment cf = null;
        Map<String, String> v = null;
        if (t instanceof ASTAdd) {
            cf = ((ASTAdd) t).getTransplant();
            v = ((ASTAdd) t).getVarMapping();
        }
        if (t instanceof ASTReplace) {
            cf = ((ASTReplace) t).getTransplant();
            v = ((ASTReplace) t).getVarMapping();
        }

        if (cf != null) {
            TransplantInfo ti = new TransplantInfo();
            ti.setPosition(cf.positionString());
            ti.setIndex(t.getIndex());
            ti.setSource(cf.codeFragmentString());
            ti.setSpoonType(cf.getCodeFragmentTypeSimpleName());
            ti.setType(t instanceof ASTReplace ? "replace" : "add");
            ti.setVariableMap(getVariableMapStr(v));
            ti.setTransplantationPoint(this);
            ti.setTransformation(t);
            transplants.add(ti);
        } else {
            TransplantInfo delete = new TransplantInfo();
            delete.setType("delete");
            delete.setIndex(t.getIndex());
            delete.setTransplantationPoint(this);
            delete.setTransformation(t);
            transplants.add(delete);
        }
    }

    /**
     * Reads a set of transformations from a JSON file in
     *
     * @param program  Program where the transformations where performed
     * @param jsonPath JSON file path
     * @return A list of transformations
     */
    public static Collection<TransformationInfo> fromJSON(String jsonPath, InputProgram program) {
        JsonSosiesInput input = new JsonSosiesInput(jsonPath, program);
        Collection<Transformation> f = input.read();
        return fromTransformations(f, input.getLoadMessages());
    }

    /*
     * Reads a set of transformations from a stream with a JSON file in it. Also, recovers the log messages raised
     * @param program Program where the transformations where performed
     * @param stream Stream with the JSON file in it
     * @param logMsgs Log messages raised
     * @return A list of transformations
    public static Collection<TransformationInfo> fromJSON(InputStreamReader stream, InputProgram program,
                                                          List<String> logMsgs) {
        JsonSosiesInput input = new JsonSosiesInput(stream, program);
        if ( logMsgs != null ) logMsgs.addAll(input.getLoadMessages());
        return fromTransformations(input.read());
    }*/

    /*
     * Reads a set of transformations from a stream with a JSON file in it
     * @param program Program where the transformations where performed
     * @param stream Stream with the JSON file in it
     * @return A list of transformations
    public static Collection<TransformationInfo> fromJSON(InputStreamReader stream, InputProgram program) {
        return fromJSON(stream, program, null);
    }*/

    /**
     * Returns a collection of TransformationInfo out a collection of Transformations
     *
     * @param transformations A collection of transformations
     * @return A list of transformations
     */
    public static Collection<TransformationInfo> fromTransformations(
            Collection<Transformation> transformations, Collection<String> msgs) {

        //Parsing of the logs and indexing them in the messages
        HashMap<UUID, ArrayList<String>> parsings = new HashMap<>();
        Pattern p = Pattern.compile(
                "Transf [a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
        for (String s : msgs) {
            if (s.startsWith("ERROR") || s.startsWith("WARNING") && s.contains("Transf")) {
                Matcher m = p.matcher(s);
                if (m.find()) {
                    UUID index = UUID.fromString(m.group().split(" ")[1]);
                    if (parsings.containsKey(index)) parsings.get(index).add(s);
                    else parsings.put(index, new ArrayList<>(Arrays.asList(new String[]{s})));
                }
            }
        }

        //Load the transformations info
        HashMap<String, TransformationInfo> r = new HashMap<>();

        for (Transformation t : transformations) {
            if (t instanceof ASTTransformation) {
                ASTTransformation astt = (ASTTransformation) t;
                String pos = astt.getTransplantationPoint().positionString();
                TransformationInfo ti = null;
                if (!r.containsKey(pos)) {
                    ti = new TransformationInfo();
                    ti.fromTransformation(astt);
                    if (parsings.containsKey(astt.getIndex()))
                         ti.getLogMessages().addAll(parsings.get(astt.getIndex()));
                    r.put(pos, ti);
                } else {
                    ti = r.get(pos);
                    if (parsings.containsKey(astt.getIndex()))
                        ti.getLogMessages().addAll(parsings.get(astt.getIndex()));
                    ti.appendTransplant(astt);
                }
            }
        }

        return r.values();
    }

    /**
     * Get the number of visible transplants
     *
     * @return The number of visible transplants
     */
    public int getVisibleTransplants() {
        int i = 0;
        for (TransplantInfo t : getTransplants()) {
            if (t.isVisible()) i++;
        }
        return i;
    }

    public String getStoragePosition() {
        return storagePosition;
    }

    public void setStoragePosition(String storagePosition) {
        this.storagePosition = storagePosition;
    }

    public String getStorageSource() {
        return storageSource;
    }

    public void setStorageSource(String storageSource) {
        this.storageSource = storageSource;
    }

    public void setLogMessages(List<String> logMessages) {
        this.logMessages = logMessages;
    }

    public List<String> getLogMessages() {
        if ( logMessages == null ) logMessages = new ArrayList<>();
        return logMessages;
    }

    public static Collection<Transformation> toTransformations(Collection<TransformationInfo> infos) {
        ArrayList<Transformation> result = new ArrayList<>();
        for ( TransformationInfo info : infos ) {
            for ( TransplantInfo t : info.getTransplants() ) {
                result.add(t.getTransformation());
            }
        }
        return result;
    }

    public float strength() {
        float result = 0;
        for ( TransplantInfo v : getTransplants() ) {
            result = v.strength();
        }
        return result;
    }

    public double getMeanDepth() {
        if ( getTests() == null || getTests().size() == 0 ) return 0.0;
        int md = 0;
        for (Map.Entry<TestInfo, PertTestCoverageData> t : getTests().entrySet() ) {
            md += t.getValue().getMeanDepth();
        }
        return md / getTests().size();
    }
}
