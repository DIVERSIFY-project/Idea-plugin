package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class MethodCallReplace extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTReplace) {
            CtElement e = ((ASTReplace)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            CtElement r = ((ASTReplace)transform.getTransformation()).getTransplant().getCtCodeFragment();
            return hasElementOfType(e, CtInvocation.class) || hasElementOfType(r, CtInvocation.class);
        }
        return false;
    }

    @Override
    protected int calculateValue(Transplant transform) {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Method call replaced";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
