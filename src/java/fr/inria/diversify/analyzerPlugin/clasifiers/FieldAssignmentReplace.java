package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by marodrig on 24/10/2014.
 */
public class FieldAssignmentReplace extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTReplace) {
            CtElement e = ((ASTReplace)transform.getTransformation()).getTransplant().getCtCodeFragment();
            CtElement r = ((ASTReplace)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            return getFieldAssignments(r).size() > 0 || getFieldAssignments(e).size() > 0;
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
        return "Field assignment replaced";
    }

}