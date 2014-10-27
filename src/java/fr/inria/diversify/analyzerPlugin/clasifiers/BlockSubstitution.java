package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.declaration.CtElement;

/**
 *
 * Gives a bad weight to TP being substituted for a statement containing a block.
 *
 * Created by marodrig on 07/10/2014.
 */
public class BlockSubstitution extends StatementSubstitution {

    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected int calculateValue(Transplant transplant) {
        ASTReplace replace = (ASTReplace) transplant.getTransformation();

        CtElement cf = replace.getTransplant().getCtCodeFragment();

        if ( cf.getElements(blockFilter).size() > 0 ) {
            return getWeight();
        }

        return 0;
    }

    @Override
    public String getDescription() {
        return "Block substitution";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
