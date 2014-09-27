package fr.inria.diversify.analyzerPlugin;

/**
 * Created by marodrig on 04/09/2014.
 */
public class AssertRepresentation extends CodePosition {

    public AssertRepresentation(String pos) {
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
        return o instanceof AssertRepresentation && toString().equals(o.toString());
    }
}
