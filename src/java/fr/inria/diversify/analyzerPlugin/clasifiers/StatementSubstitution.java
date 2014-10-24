package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.TransformationRepresentation;
import fr.inria.diversify.analyzerPlugin.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Gives a bad weight to Transplant being a Statement (Not block, not var declaration) substitution .
 * <p/>
 * Created by marodrig on 07/10/2014.
 */
public abstract class StatementSubstitution extends TransformClasifier {

    protected static TypeFilter blockFilter = new TypeFilter(CtMethod.class);

    @Override
    protected boolean canClassify(Transplant transplant) {
        //The sosie transformation is inside the Transplant UI representation
        return (transplant.getTransformation() instanceof ASTReplace);
    }
}
