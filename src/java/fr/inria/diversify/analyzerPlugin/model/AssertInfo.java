package fr.inria.diversify.analyzerPlugin.model;

/**
 * Created by marodrig on 04/09/2014.
 */
public class AssertInfo extends CodePosition {

    public AssertInfo(String pos) {
        setPosition(pos);
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
        return o instanceof AssertInfo && toString().equals(o.toString());
    }
}
