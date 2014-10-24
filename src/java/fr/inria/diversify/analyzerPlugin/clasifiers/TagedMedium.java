package fr.inria.diversify.analyzerPlugin.clasifiers;

/**
 * Created by marodrig on 10/10/2014.
 */
public class TagedMedium extends TagedTransform {
    public TagedMedium() {
        tagClassification = "medium";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
