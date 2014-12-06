package fr.inria.diversify.analyzerPlugin.model;

/**
 * Created by marodrig on 06/12/2014.
 */
public class PertTestCoverageData {

    private int hits = 0;
    private Integer stackMaxDepth = null;
    private Integer stackMeanDepth = null;
    private Integer stackMinDepth = null;
    private Integer maxDepth = null;
    private Integer meanDepth = null;
    private Integer minDepth = null;

    TestRepresentation test;

    public PertTestCoverageData(TestRepresentation test) {
        this.test = test;
    }

    public void addHits(int hits) {
        this.hits += hits;
    }

    public void setStackMaxDepth(int stackMaxDepth) {
        this.stackMaxDepth = stackMaxDepth;
    }

    public int getStackMaxDepth() {
        return stackMaxDepth;
    }

    public void setStackMeanDepth(int stackMeanDepth) {
        this.stackMeanDepth = stackMeanDepth;
    }

    public int getStackMeanDepth() {
        return stackMeanDepth;
    }

    public void setStackMinDepth(int stackMinDepth) {
        this.stackMinDepth = stackMinDepth;
    }

    public int getStackMinDepth() {
        return stackMinDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMeanDepth(int meanDepth) {
        this.meanDepth = meanDepth;
    }

    public int getMeanDepth() {
        return meanDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setDepth(int depth) {
        if (meanDepth == null) meanDepth = depth;
        if (maxDepth == null) maxDepth = depth;
        if (minDepth == null) minDepth = depth;
    }

    public void setStackDepth(int stackDepth) {
        if (stackMeanDepth == null) stackMeanDepth = stackDepth;
        if (stackMaxDepth == null) stackMaxDepth = stackDepth;
        if (stackMinDepth == null) stackMinDepth = stackDepth;
    }

    public TestRepresentation getTest() {
        return test;
    }

    public int getHits() {
        return hits;
    }
}
