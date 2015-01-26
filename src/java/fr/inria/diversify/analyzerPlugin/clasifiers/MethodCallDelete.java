package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTDelete;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class MethodCallDelete extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if ( transform.getTransformation() instanceof ASTDelete) {
            CtElement e = ((ASTDelete)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            return hasElementOfType(e, CtInvocation.class);
        }
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Method call deleted";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
