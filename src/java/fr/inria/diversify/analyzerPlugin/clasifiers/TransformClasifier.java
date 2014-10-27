package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.QueryVisitor;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Assigns a numeric value to a transform to weight it according to a trait. Higher values goes to transform conforming
 * the most to a trait (e.g. "undetectable by good tests") a zero value goes to a transform NOT having such trait and negatives to
 * transform being the opposite (e.g. "detectable by good tests")
 * <p/>
 * Created by marodrig on 06/10/2014.
 */
public abstract class TransformClasifier {

    protected static int STRONG = 10;
    protected static int MEDIUM = 5;
    protected static int WEAK = 1;

    /**
     * indicate if this class is just user interface commodity filter or if it s a real classification function
     */
    public abstract boolean isUserFilter();

    /**
     * Indicates if the transformation can be classified or not
     *
     * @param transform Transform to be classified
     * @return True if can, false if not
     */
    protected abstract boolean canClassify(Transplant transform);

    /**
     * Actually calculate the classification value of the transform
     *
     * @param transform transform to be classified
     * @return An integer value weighting the compliance to an given trait
     */
    protected abstract int calculateValue(Transplant transform);

    /**
     * Calculate the classification value of the transform
     *
     * @param transform transform to be classified
     * @return An integer value weighting the compliance to an given trait
     */
    public int value(Transplant transform) {
        if (canClassify(transform)) {
            return calculateValue(transform);
        }
        return 0;
    }

    public abstract String getDescription();

    public abstract int getWeight();

    /**
     * Returns all the field assignments in e
     * @param e
     * @return
     */
    protected List<CtElement> getFieldAssignments(CtElement e) {
        ArrayList<CtElement> result = new ArrayList<CtElement>();
        List<CtElement> assigns = getElementsOfType(e, CtAssignment.class);
        for ( CtElement a : assigns ) {
            if ( hasElementOfType(a, CtFieldAccess.class) ) {
                result.add(a);
            }
        }
        return result;
    }

    protected boolean hasElementOfType(CtElement e, Class<?> toQuery) {

        return toQuery.isAssignableFrom(e.getClass()) || getElementsOfType(e, toQuery).size() > 0;
    }

    /**
     * Returns the childs elements of a given type. More general than the get elements of CtElements
     * @param e
     * @return
     */
    protected List<CtElement> getElementsOfType(CtElement e, Class toQuery) {
        TypeFilter assignFilter = new TypeFilter(toQuery);
        QueryVisitor<CtElement> assignQuery = new QueryVisitor<CtElement>(assignFilter);
        assignQuery.scan(e);
        return assignQuery.getResult();
    }
}
