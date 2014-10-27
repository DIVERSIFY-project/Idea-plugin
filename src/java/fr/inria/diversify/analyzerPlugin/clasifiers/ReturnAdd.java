package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTAdd;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class ReturnAdd extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTAdd ) {
            CtElement e = ((ASTAdd)transform.getTransformation()).getTransplant().getCtCodeFragment();
            return hasElementOfType(e, CtReturn.class);
        }
        return false;
    }

    @Override
    protected int calculateValue(Transplant transform) {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Return added";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
