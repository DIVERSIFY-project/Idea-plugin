package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTAdd;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by marodrig on 24/10/2014.
 */
public class InnocuousMethodCallAdd extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTAdd ) {
            CtElement e = ((ASTAdd)transform.getTransformation()).getTransplant().getCtCodeFragment();
            List<CtElement> invocations = getElementsOfType(e, CtInvocation.class);
            for ( CtElement inv : invocations ) {
                if ( hasElementOfType(inv, CtInvocation.class) || getFieldAssignments(e).size() > 0 ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected int calculateValue(Transplant transform) {
        return getWeight();
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Method call added";
    }

}
