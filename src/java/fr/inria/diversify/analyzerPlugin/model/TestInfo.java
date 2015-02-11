package fr.inria.diversify.analyzerPlugin.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by marodrig on 04/09/2014.
 */
public class TestInfo extends CodePosition {

    private HashSet<AssertInfo> asserts;

    private long endTime;

    public TestInfo() {
        asserts = new HashSet<AssertInfo>();
    }

    public void fromLogString(String line) {
        int pos = line.indexOf(";") + 1;
        setPosition(line.substring(pos, line.length() - 3));
    }

    public Set<AssertInfo> getAsserts() {
        return asserts;
    }

    @Override
    public String toString() {
        return  getPosition();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TestInfo && toString().equals(o.toString());
    }

    @Override
    public String getSource() {
        return getPosition();
    }


    /**
     * Millis when the test ends. help to identify multi-threaded TP calls in different log files
     * @return
     */
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
