package fr.inria.diversify.analyzerPlugin.model;

import fr.inria.diversify.transformation.ast.ASTTransformation;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents Transplantation point. Contains the data of the transformation needed for the plugin
 * <p/>
 * Created by marodrig on 04/09/2014.
 */
public class TransformationRepresentation extends CodePosition {

    private int totalTestHits;

    private int appliedTransformIndex = -1;

    public int getTotalAssertionHits() {
        return totalAssertionHits;
    }

    public void setTotalAssertionHits(int totalAssertionHits) {
        this.totalAssertionHits = totalAssertionHits;
    }

    /**
     * Total hits of this TP
     */
    private int hits = 0;

    private int totalAssertionHits = 0;

    /**
     * Increments the hits of the TP
     *
     * @param i number of increments
     */
    public void incHits(int i) {
        hits = hits + i;
    }

    public Set<TestRepresentation> getTests() {
        return tests;
    }

    public void setTest(Set<TestRepresentation> test) {
        this.tests = test;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    private String position;

    private String spoonType;

    private String variableMapping;

    private List<Transplant> transplants;

    private Set<TestRepresentation> tests;

    private String type;

    private String source;

    private HashMap<AssertRepresentation, Integer> assertCount;

    private HashMap<TestRepresentation, Integer> testCount;

    /*
    public TransformationRepresentation(String potPosition) {
        this.position = potPosition;
        transplants = new ArrayList<Transplant>();
    }*/

    /**
     * Extract a series of data out of the json object
     */
    public TransformationRepresentation() {
        transplants = new ArrayList<Transplant>();
        tests = new HashSet<TestRepresentation>();
        assertCount = new HashMap<AssertRepresentation, Integer>();
        testCount = new HashMap<TestRepresentation, Integer>();
    }

    /**
     * Initializes the representation from the JSON object
     */
    public void fromJSONObject(JSONObject object, JSONObject tags) throws JSONException {
        JSONObject tp = object.getJSONObject("transplantationPoint");
        setSource(tp.getString("sourceCode"));
        setPosition(tp.getString("position"));
        setSpoonType(tp.getString("type"));
        setType(object.getString("name"));

        appendTransplant(object, tags);

    }

    /**
     * Returns the number of times an instrumented test method executed and hit this TP
     *
     * @param rep Assert that we want to query for
     * @return
     */
    public int getTestHits(TestRepresentation rep) {
        return testCount.containsKey(rep) ? testCount.get(rep) : 0;
    }

    /**
     * Returns the number of times an assertions was executed after this TP was hit
     *
     * @param rep Assert that we want to query for
     * @return
     */
    public int getAssertHits(AssertRepresentation rep) {
        return assertCount.containsKey(rep) ? assertCount.get(rep) : 0;
    }

    /**
     * Add an assertion hits to the TP
     *
     * @param ar   assertion hit after the TP
     * @param hits number of hits
     */
    public void addAssertHit(AssertRepresentation ar, int hits) {
        addHits(assertCount, ar, hits);
        setTotalAssertionHits(getTotalAssertionHits() + hits);
    }


    /**
     * Add an test hits to the TP
     *
     * @param test assertion hit after the TP
     * @param hits number of hits
     */
    public void addTestHit(TestRepresentation test, int hits) {
        if (!this.tests.contains(test)) {
            tests.add(test);
        }
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
    public Collection<AssertRepresentation> getAsserts() {
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
     * Extract the transplant out of the JSON object and appends it to the list of TP over this point
     *
     * @param jt   JSON with the transformation
     * @param tags Array with the tags to every transformation
     */
    public void appendTransplant(JSONObject jt, JSONObject tags) throws JSONException {
        if (jt.has("transplant")) {
            JSONObject tp = jt.getJSONObject("transplant");
            Transplant t = new Transplant();
            t.setPosition(tp.getString("position"));
            t.setSource(tp.getString("sourceCode"));
            t.setSpoonType(tp.getString("type"));
            t.setIndex(jt.getInt("tindex"));
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
            Transplant delete = new Transplant();
            delete.setType("delete");
            delete.setIndex(jt.getInt("tindex"));
            delete.setTransplantationPoint(this);
            transplants.add(delete);
        }
    }

    /**
     * Convert the tags of all transplant into a JSON ArrayList.
     * This is used to store all tags that annotates the Transplants of this TP
     *
     * @param representations
     * @return a JSONObject ArrayList with the Transformations serialized
     */
    public static JSONObject tagsToJSON(Collection<TransformationRepresentation> representations) throws JSONException {

        JSONObject result = new JSONObject();
        for (TransformationRepresentation r : representations) {
            for (Transplant t : r.getTransplants()) {
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

    public List<Transplant> getTransplants() {
        return transplants;
    }

    public void setTransplants(List<Transplant> transplantPositions) {
        this.transplants = transplantPositions;
        for (Transplant t : transplantPositions) {
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
    private void restoreSourceFile(ASTTransformation transf, String srcDir, String destDir) throws IOException {
        String filePath = transf.getTransplantationPoint().getSourceClass().getQualifiedName().replaceAll("\\.", "/");
        String sourcePath = srcDir + File.separator + filePath + ".java";
        filePath = destDir + File.separator + filePath + "." + "java.backup";
        FileUtils.copyFile(new File(filePath), new File(sourcePath));
    }

    public boolean isTransplantApplied(Transplant transplant) {
        return appliedTransformIndex == transplants.indexOf(transplant);
    }

    /**
     * Switch back and forth from the original code to the transformed code
     *
     * @param srcDir  Source code to be transformed
     * @param destDir Destination dir where the transformation code is going to be stored
     * @throws Exception
     */
    public void switchTransformation(Transplant transplant, String srcDir, String destDir) throws Exception {

        int index = transplants.indexOf(transplant);

        //We work only with ASTTransformations
        ASTTransformation transf = (ASTTransformation) transplant.getTransformation();

        if (index == appliedTransformIndex) {
            restoreSourceFile(transf, srcDir, destDir);
            appliedTransformIndex = -1;
        } else {
            appliedTransformIndex = index;
            if (appliedTransformIndex == -1) {
                throw new IndexOutOfBoundsException("This transplant does not belogs to the TP");
            }
            makeOriginalSourceBackUp(transf, srcDir, destDir);

            transf.apply(destDir);

            copyModifiedSource(transf, srcDir, destDir);
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
    public Transplant getAppliedTransplant() {
        if (appliedTransformIndex == -1) return null;
        return transplants.get(appliedTransformIndex);
    }

}
