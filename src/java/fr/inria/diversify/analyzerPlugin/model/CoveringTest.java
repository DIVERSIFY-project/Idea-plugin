package fr.inria.diversify.analyzerPlugin.model;

/**
 * Created by marodrig on 19/11/2014.
 */
public class CoveringTest {

    private TestRepresentation test;

    private int minDepth;

    private int maxDepth;

    private int meanDepth;

    private int hits;

    public TestRepresentation getTest() {
        return test;
    }

    public void setTest(TestRepresentation test) {
        this.test = test;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMeanDepth() {
        return meanDepth;
    }

    public void setMeanDepth(int meanDepth) {
        this.meanDepth = meanDepth;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
