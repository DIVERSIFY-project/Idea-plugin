package fr.inria.diversify.analyzerPlugin.ut.component;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

/**
 * Created by marodrig on 02/02/2015.
 */
public class NonReplaceClassifier extends TransformClassifier {
    /**
     * indicate if this class is just user interface commodity filter or if it s a real classification function
     */
    @Override
    public boolean isUserFilter() {
        return false;
    }

    /**
     * Indicates if the transformation can be classified or not
     *
     * @param transform Transform to be classified
     * @return True if can, false if not
     */
    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if ( transform.getType() != null ) {
            return !transform.getType().contains("replace");
        }
        return false;
    }

    /**
     * Actually calculate the classification value of the transform
     *
     * @param transform transform to be classified
     * @return An integer value weighting the compliance to an given trait
     */
    @Override
    protected int calculateValue(TransplantInfo transform) {
        return getWeight();
    }

    @Override
    public String getDescription() {
        return "If classifier";
    }

    @Override
    public int getWeight() {
        return TransformClassifier.WEAK;
    }
}
