package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.syringe.processor.EntryLog;
import fr.inria.diversify.syringe.processor.EntryProcessor;
import fr.inria.diversify.syringe.processor.LoadingException;

import java.util.*;

import static fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageEntry.*;

/**
 * Created by marodrig on 03/02/2015.
 */
public class EnhancedCoverageProcessor implements EntryProcessor {

    private int testExecutedCount;
    private int testExecutedCoveringATPCount;
    private int totalPotsHitsCount;
    private int assertionsExecutedCount;
    private int assertionsExecutedCoveringCount;
    private int assertionsDeclared;
    private int assertDeclaredCoveringATPCount;

    private HashMap<String, TransformationInfo> representations;

    /**
     * Errors during the processing of the coverage
     */
    private List<String> errors;

    public EnhancedCoverageProcessor(Collection<TransformationInfo> representations) {
        this.representations = new HashMap<>();
        for ( TransformationInfo r : representations ) {
            this.representations.put(r.getPosition(), r);
        }
    }

    @Override
    public void process(Collection<EntryLog> entries) throws LoadingException {
        TestInfo currentTest = null;

        errors = new ArrayList<>();

        HashSet<TransformationInfo> tcpThisTest = new HashSet<TransformationInfo>(); //Transplant point reached in this test
        HashMap<String, AssertInfo> assertsThisTest = new HashMap<String, AssertInfo>();

        testExecutedCount = 0;
        testExecutedCoveringATPCount = 0;


        totalPotsHitsCount = 0;

        assertionsExecutedCount = 0;
        assertionsExecutedCoveringCount = 0;

        assertionsDeclared = 0;
        assertDeclaredCoveringATPCount = 0;

        //HashSet<String> assertsExecuted = new HashSet<String>();
        HashSet<String> coveringAsserts = new HashSet<String>();
        HashSet<String> coveringTests = new HashSet<String>();
        HashSet<TestInfo> declaredTest = new HashSet<TestInfo>();


        //Move entries to elements
        int iteration = 0;
        for (EntryLog entry : entries) {
            EnhancedCoverageEntry el = (EnhancedCoverageEntry)entry;
            iteration++;
            try {
                if (el.getType().equals(NEW_TEST)) {
                    currentTest = new TestInfo();
                    currentTest.setPosition(el.getPosition());
                    currentTest.setRegisterTime(el.getMillis());
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
                    if (el.getType().equals(TP)) {
                        TestInfo test = currentTest;
                        //Obtain the TP by its position
                        //Integer index = Integer.parseInt(idMap.get(Integer.parseInt(el.position)));
                        TransformationInfo r = getRepresentations().get(el.getPosition());
                        r.incHits(1);
                        totalPotsHitsCount++;
                        if (!tcpThisTest.contains(r)) tcpThisTest.add(r);

                        if (test != null) {
                            //Counts the test hit
                            r.addTestHit(test, 1);
                            //r.setDepth(test, el.maxDepth, el.stackMaxDepth);
                            r.setDepth(test, el.getMaxDepth(), -1);
                        }
                    } else if (el.getType().equals(ASSERT)) {
                        assertionsExecutedCount++;
                        AssertInfo ar = new AssertInfo(el.getPosition());
                        assertsThisTest.put(el.getPosition(), ar);
                        currentTest.getAsserts().add(ar);
                        //Include this assert in the asserts of all TP in the log
                        for (TransformationInfo t : tcpThisTest) {
                            t.addAssertHit(ar, 1);
                        }

                        //Count asserts covering at least one TP
                        if (tcpThisTest.size() > 0) {
                            assertionsExecutedCoveringCount = getAssertionsExecutedCoveringCount() + 1;
                        }

                    } else if (el.getType().equals(TP_COUNT)) {
                        //Integer index = Integer.parseInt(idMap.get(Integer.parseInt(el.position)));
                        TransformationInfo r = getRepresentations().get(el.getPosition());
                        int k = el.getExecutions();
                        totalPotsHitsCount += k;
                        r.incHits(k);
                        r.updateDepth(currentTest, el.getMinDepth(), el.getMeanDepth(), el.getMaxDepth(), -1, -1, -1);
                    } else if (el.getType().equals(ASSERT_COUNT)) {
                        AssertInfo ar = assertsThisTest.get(el.getPosition());
                        int hits = el.getExecutions();
                        assertionsExecutedCount += hits;

                        //Count asserts covering at least one TP
                        if (tcpThisTest.size() > 0) {
                            assertionsExecutedCoveringCount = assertionsExecutedCoveringCount + hits - 1;
                        }

                        for (TransformationInfo t : tcpThisTest) {
                            //Don't add asserts hits to TP that don't have them
                            if (t.getAssertHits(ar) > 0) {
                                t.addAssertHit(ar, hits - 1);
                            }
                        }
                    } else if (el.getType().equals(END_TEST)) {
                        currentTest.setEndTime(el.getMillis());
                    }
                }
            } catch (Exception e) {
                errors.add("At iteration " + iteration + ". Got exception " +
                        e.getClass().getSimpleName() + ". Msg: " + e.getMessage() );
                //throw new LoadingException(iteration, "Error processing", e);
            }
        }
    }

    public int getTestExecutedCount() {
        return testExecutedCount;
    }

    public int getTestExecutedCoveringATPCount() {
        return testExecutedCoveringATPCount;
    }

    public int getTotalPotsHitsCount() {
        return totalPotsHitsCount;
    }

    public int getAssertionsExecutedCount() {
        return assertionsExecutedCount;
    }

    public int getAssertionsExecutedCoveringCount() {
        return assertionsExecutedCoveringCount;
    }

    public int getAssertionsDeclared() {
        return assertionsDeclared;
    }

    public int getAsserstDeclaredCoveringATPCount() {
        return assertDeclaredCoveringATPCount;
    }

    public HashMap<String, TransformationInfo> getRepresentations() {
        return representations;
    }

    public List<String> getErrors() {
        if ( errors == null ) errors = new ArrayList<>();
        return errors;
    }
}
