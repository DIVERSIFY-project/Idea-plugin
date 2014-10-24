package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.Transplant;

/**
 * Created by marodrig on 10/10/2014.
 */
public abstract class TagedTransform extends TransformClasifier {

    @Override
    public boolean isUserFilter() {
        return true;
    }

    //The tag we are looking for
    protected String tagClassification;

    @Override
    protected boolean canClassify(Transplant transform) {
        return true;
    }

    @Override
    protected int calculateValue(Transplant transform) {
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
