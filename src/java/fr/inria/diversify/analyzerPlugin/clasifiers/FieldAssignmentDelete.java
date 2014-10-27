package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by marodrig on 24/10/2014.
 */
public class FieldAssignmentDelete extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTDelete) {
            CtElement e = ((ASTDelete)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            return getFieldAssignments(e).size() > 0;
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
        return "Field assignment deleted";
    }

}
