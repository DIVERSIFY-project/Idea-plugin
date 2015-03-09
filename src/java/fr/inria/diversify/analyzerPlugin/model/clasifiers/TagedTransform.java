package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

/**
 * Created by marodrig on 10/10/2014.
 */
public abstract class TagedTransform extends TransformClassifier {

    @Override
    public boolean isUserFilter() {
        return true;
    }

    //The tag we are looking for
    protected String tagClassification;

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        return true;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        if ( transform.getTags() == null ) return 0;
        String[] tags = transform.getTags().split(",");
        for (String tag : tags ) {
            if ( tag.toLowerCase().contains(tagClassification) ) {
                return getWeight();
            }
        }
        return 0;
    }

    @Override
    public String getDescription() {
        return "Taged " + tagClassification;
    }
}
