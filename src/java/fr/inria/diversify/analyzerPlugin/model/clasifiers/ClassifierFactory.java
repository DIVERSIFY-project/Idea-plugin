package fr.inria.diversify.analyzerPlugin.model.clasifiers;

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
    public List<TransformClassifier> buildClassifiers() {
        ArrayList<TransformClassifier> clasifiers = new ArrayList<TransformClassifier>();

        //clasifiers.add(new AssignNotUsedLocally());
        //clasifiers.add(new StatementByLiteral());
        //clasifiers.add(new SingleStatementSubstitution());
        //clasifiers.add(new DeleteSubstitution());

        //Query
        clasifiers.add(new QueryExtendedCoverage());

        //Strong classifiers
        clasifiers.add(new TagedStrong());
        clasifiers.add(new InnocuousMethodCallAdd());
        clasifiers.add(new InnocuousMethodCallReplace());
        clasifiers.add(new InnocuousMethodCallDelete());

        //Medium classifiers
        clasifiers.add(new TagedMedium());
        clasifiers.add(new ReturnAdd());
        clasifiers.add(new ReturnReplace());
        clasifiers.add(new ReturnDelete());

        clasifiers.add(new MethodCallAdd());
        clasifiers.add(new MethodCallReplace());
        clasifiers.add(new MethodCallDelete());

        clasifiers.add(new BlockAdd());
        clasifiers.add(new BlockReplace());
        clasifiers.add(new BlockDelete());

        clasifiers.add(new FieldAssignmentAdd());
        clasifiers.add(new FieldAssignmentReplace());
        clasifiers.add(new FieldAssignmentDelete());

        clasifiers.add(new StatementAdd());
        clasifiers.add(new StatementReplace());
        clasifiers.add(new StatementDelete());

        //Weak classifiers
        clasifiers.add(new TagedWeak());
        clasifiers.add(new LocalVarDeclaration());
        clasifiers.add(new ExceptionByException());
        clasifiers.add(new Fake());

        clasifiers.add(new DeepFrequentlyTested());
        clasifiers.add(new DeepSeldomTested());
        clasifiers.add(new ShallowFrequentlyTested());
        clasifiers.add(new ShallowSeldomTested());

        return clasifiers;
    }

}
