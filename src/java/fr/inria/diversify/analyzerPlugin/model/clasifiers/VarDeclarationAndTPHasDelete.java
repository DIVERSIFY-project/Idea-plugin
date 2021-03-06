package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtElement;

/**
 *
 * Gives a bad weight to Transplant being a var declarationa and who's TP has also a delete Transplant.
 *
 * These bad weight are given because most probably the transplant has no importance at all. The effect of the sosie is
 * simply by deleting the TP
 *
 * Created by marodrig on 07/10/2014.
 */
public class VarDeclarationAndTPHasDelete extends TransformClassifier {

    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transplant) {
        //The sosie transformation is inside the Transplant UI representation
        return (transplant.getTransformation() instanceof ASTReplace);
    }

    @Override
    protected int calculateValue(TransplantInfo transplant) {
        ASTReplace replace = (ASTReplace)transplant.getTransformation();

        CtElement cf = replace.getTransplant().getCtCodeFragment();

        //CtNewClass example : Map<String, Int> a = new HashMap<String, Int>();
        if ( cf instanceof CtLocalVariable || cf instanceof CtNewClass ) {
            TransformationInfo tr = transplant.getTransplantationPoint();
            for ( TransplantInfo t : tr.getTransplants() ) {
                if ( t.getType().equals("delete") ) {
                    return getWeight();
                }
            }
        }

        return 0;
    }

    @Override
    public String getDescription() {
        return "Transplant is var declaration. Parent has delete.";
    }

    @Override
    public int getWeight() {
        return USELESS;
    }
}
