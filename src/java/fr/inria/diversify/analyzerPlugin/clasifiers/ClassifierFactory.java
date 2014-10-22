package fr.inria.diversify.analyzerPlugin.clasifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marodrig on 07/10/2014.
 */
public class ClassifierFactory {

    /**
     * Build all classifiers
     * @return A list with all the classifiers in the package fr.inria.diversify.analyzerPlugin
     */
    public List<TransformClasifier> buildClassifiers() {
        ArrayList<TransformClasifier> clasifiers = new ArrayList<TransformClasifier>();
        clasifiers.add(new TagedStrong());
        clasifiers.add(new TagedMedium());
        clasifiers.add(new TagedWeak());
        clasifiers.add(new ExceptionByException());
        clasifiers.add(new VarDeclarationAndTPHasDelete());
        clasifiers.add(new StatementByLiteral());
        clasifiers.add(new BlockSubstitution());
        clasifiers.add(new SingleStatementSubstitution());
        clasifiers.add(new DeleteSubstitution());
        clasifiers.add(new AssignNotUsedLocally());
        return clasifiers;
    }

}
