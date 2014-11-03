package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * Created by marodrig on 24/10/2014.
 */
public class InnocuousMethodCallDelete extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        if (transform.getTransformation() instanceof ASTDelete) {
            CtElement e = ((ASTDelete) transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            if (transform.getContainsInnocuousCalls() == null) {
                boolean b = containsInnocuousInvocation(e);
                transform.setContainsInnocuousCalls(b);
            } else {
                return transform.getContainsInnocuousCalls();
            }
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
        return "Innocuous method call deleted";
    }

}
