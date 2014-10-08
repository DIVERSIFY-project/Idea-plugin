package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.TransformationRepresentation;
import fr.inria.diversify.analyzerPlugin.Transplant;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtThrow;
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
public class VarDeclarationAndTPHasDelete extends TransformClasifier {

    private int WEIGHT = -4;

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
        if ( cf instanceof CtLocalVariable || cf instanceof CtNewClass ) {
            TransformationRepresentation tr = transplant.getTransplantationPoint();
            for ( Transplant t : tr.getTransplants() ) {
                if ( t.getType().equals("delete") ) {
                    return WEIGHT;
                }
            }
        }

        return 0;
    }

    @Override
    public String getDescription() {
        return "Transplant is var declaration. Parent has delete.";
    }
}
