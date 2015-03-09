package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTAdd;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class InnocuousMethodCallAdd extends TransformClassifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if (transform.getTransformation() instanceof ASTAdd) {
            CtElement e = ((ASTAdd) transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            CtElement r = ((ASTAdd) transform.getTransformation()).getTransplant().getCtCodeFragment();
            return containsInnocuousInvocation(e) || containsInnocuousInvocation(r);
        }
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return getWeight();
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Innocuous method call added";
    }

}
