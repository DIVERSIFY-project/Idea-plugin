package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.Transplant;
import fr.inria.diversify.transformation.ast.ASTReplace;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.visitor.Filter;

/**
 * Gives little weight to a statement exchanged by a literal
 *
 * Created by marodrig on 08/10/2014.
 */
public class StatementByLiteral extends TransformClasifier {

    private static Filter<CtLiteral> filter = new Filter<CtLiteral>() {
        @Override
        public boolean matches(CtLiteral element) {
            return true;
        }

        @Override
        public Class<?> getType() {
            return CtLiteral.class;
        }
    };

    @Override
    protected boolean canClassify(Transplant transplant) {
        //The sosie transformation is inside the Transplant UI representation
        return (transplant.getTransformation() instanceof ASTReplace);
    }

    @Override
    protected int calculateValue(Transplant transplant) {
        ASTReplace replace = (ASTReplace)transplant.getTransformation();
        if ( replace.getTransplant().getCtCodeFragment().getElements(filter).size() > 0) {
            return 2;
        }
        return 0;
    }

    @Override
    public String getDescription() {
        return "Replacement contains a literal";
    }
}
