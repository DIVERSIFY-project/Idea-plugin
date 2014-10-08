package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.Transplant;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;


/**
 * Gives negative weight to the transformations changing exceptions for exception.
 * <p/>
 * Exceptions substituted by another are probably never checked.
 * <p/>
 * Created by marodrig on 06/10/2014.
 */
public class ExceptionByException extends TransformClasifier {

    private int WEIGHT = -5;

    @Override
    protected boolean canClassify(Transplant transplant) {
        //The sosie transformation is inside the Transplant UI representation
        return (transplant.getTransformation() instanceof ASTReplace);
    }

    @Override
    protected int calculateValue(Transplant transplant) {
        ASTReplace replace = (ASTReplace) transplant.getTransformation();

        CtElement ctTP = replace.getTransplantationPoint().getCtCodeFragment();
        CtElement ctT = replace.getTransplant().getCtCodeFragment();
        String spoonTP = transplant.getSpoonType();
        String spoonT = transplant.getTransplantationPoint().getSpoonTransformationType();

        if ((ctTP instanceof CtThrow && ctT instanceof CtThrow) ||
                (spoonT.contains("CtThrow") && spoonTP.contains("CtThrow"))) {
            return WEIGHT;
        } else {
            return 0;
        }
    }

    @Override
    public String getDescription() {
        return "Exceptions substituted by another exception";
    }
}
