package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 *
 * Gives a bad weight to Transplant being a block substitution .
 *
 * Created by marodrig on 07/10/2014.
 */
public class BlockSubstitution extends TransformClasifier {

    private int WEIGHT = -1;

    @Override
    protected boolean canClassify(Transplant transplant) {
        //The sosie transformation is inside the Transplant UI representation
        return (transplant.getTransformation() instanceof ASTReplace);
    }

    @Override
    protected int calculateValue(Transplant transplant) {
        ASTReplace replace = (ASTReplace)transplant.getTransformation();

        CtElement cf = replace.getTransplant().getCtCodeFragment();

        //CtNewClass example : Map<String, Int> a = new HashMap<String, Int>();
        if ( cf instanceof CtBlock ) {
            return WEIGHT;
        }

        return 0;
    }

    @Override
    public String getDescription() {
        return "Block substitution";
    }
}
