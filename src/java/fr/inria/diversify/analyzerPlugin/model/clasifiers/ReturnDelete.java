package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTDelete;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class ReturnDelete extends TransformClassifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if ( transform.getTransformation() instanceof ASTDelete) {
            CtElement e = ((ASTDelete)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            return hasElementOfType(e, CtReturn.class);
        }
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Return deleted";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
