package fr.inria.diversify.analyzerPlugin.model.clasifiers;

/**
 * Created by marodrig on 10/10/2014.
 */
public class TagedStrong extends TagedTransform {
    public TagedStrong() {
        tagClassification = "strong";
    }

    @Override
    public int getWeight() {
        return STRONG;
    }
}
