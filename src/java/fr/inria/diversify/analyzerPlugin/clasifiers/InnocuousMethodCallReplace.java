package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by marodrig on 24/10/2014.
 */
public class InnocuousMethodCallReplace extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if ( transform.getTransformation() instanceof ASTReplace) {
            //verify only invocations
            CtElement e = ((ASTReplace) transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            CtElement r = ((ASTReplace) transform.getTransformation()).getTransplant().getCtCodeFragment();
            return containsInnocuousInvocation(e) || containsInnocuousInvocation(r);
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
        return "Innocuous method replaced";
    }

}
