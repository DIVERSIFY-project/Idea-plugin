package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;

/**
 * Created by marodrig on 24/10/2014.
 */
public class BlockReplace extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if ( transform.getTransformation() instanceof ASTReplace) {
            CtElement e = ((ASTReplace)transform.getTransformation()).getTransplantationPoint().getCtCodeFragment();
            CtElement r = ((ASTReplace)transform.getTransformation()).getTransplant().getCtCodeFragment();
            return hasElementOfType(e, CtBlock.class) || hasElementOfType(r, CtBlock.class);
        }
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return MEDIUM;
    }

    @Override
    public String getDescription() {
        return "Block replaced";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
