package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.visitor.QueryVisitor;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Assigns a numeric value to a transform to weight it according to a trait. Higher values goes to transform conforming
 * the most to a trait (e.g. "undetectable by good tests") a zero value goes to a transform NOT having such trait and negatives to
 * transform being the opposite (e.g. "detectable by good tests")
 * <p/>
 * Created by marodrig on 06/10/2014.
 */
public abstract class TransformClassifier {

    protected static int STRONG = 10;
    protected static int MEDIUM = 5;
    protected static int WEAK = -1;
    protected static int USELESS = -5;
    protected static int PLAIN_BAD = -10;

    /**
     * indicate if this class is just user interface commodity filter or if it s a real classification function
     */
    public abstract boolean isUserFilter();

    /**
     * Indicates if the element contains an innocuous invocation.
     * <p/>
     * An innocuous invocation is an invocation that complies to the following:
     * 1. Block != null (the method inside code is known)
     * 2. Has no other invocations
     * 3. Has no Field assignments
     *
     * @param e Element to inspect
     * @return True if the element contains innocuous invocations
     */
    protected boolean containsInnocuousInvocation(CtElement e) {
        List<CtElement> invocations = getElementsOfType(e, CtInvocation.class);
        if (invocations.size() == 0) return false;
        return activeInvocationCount(invocations, 5, false) == 0;
    }

    private int activeInvocationCount(List<CtElement> invocations, int depth, boolean countAll) {
        int result = 0;
        if (depth > 0) {
            if (invocations.size() == 0) {
                return 0;
            } //Contains no invocations at all, active or positive

            //Search for active invocations
            for (CtElement inv : invocations) {
                if (!countAll && result > 0) return result;
                CtExecutable executable = ((CtInvocationImpl) inv).getExecutable().getDeclaration();

                if (executable != null ) {
                    if ( executable.getType().toString().toLowerCase().equals("void") ) {
                        CtBlock b = executable.getBody();
                        if (b == null) { //The method code cannot be found (native, external lib., etc.)
                            result++; //Since cannot be sure is not active, we count it as active
                        } else if (hasElementOfType(b, CtThrow.class) || getFieldAssignments(b).size() > 0) {
                            result++; //Definitively active
                        } else if (hasElementOfType(b, CtInvocation.class)) {
                            result += activeInvocationCount(getElementsOfType(b, CtInvocation.class), depth - 1, countAll);
                        }
                    } else {
                        result++;
                    }
                } else {//The method code cannot be found (native, external lib., etc.)
                    result++; //Since cannot be sure is not active, we count it as active
                }
            }
        }
        return result;
    }

    /**
     * Indicates if the transformation can be classified or not
     *
     * @param transform Transform to be classified
     * @return True if can, false if not
     */
    protected abstract boolean canClassify(TransplantInfo transform);

    /**
     * Actually calculate the classification value of the transform
     *
     * @param transform transform to be classified
     * @return An integer value weighting the compliance to an given trait
     */
    protected abstract int calculateValue(TransplantInfo transform);

    /**
     * Calculate the classification value of the transform
     *
     * @param transform transform to be classified
     * @return An integer value weighting the compliance to an given trait
     */
    public int value(TransplantInfo transform) {
        if (canClassify(transform)) {
            return calculateValue(transform);
        }
        return 0;
    }

    public abstract String getDescription();

    public abstract int getWeight();

    /**
     * Returns all the field assignments in e
     *
     * @param e
     * @return
     */
    protected List<CtElement> getFieldAssignments(CtElement e) {
        ArrayList<CtElement> result = new ArrayList<CtElement>();
        List<CtElement> assigns = getElementsOfType(e, CtAssignment.class);
        for (CtElement a : assigns) {
            if (hasElementOfType(a, CtFieldAccess.class)) {
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
     *
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
