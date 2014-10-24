package fr.inria.diversify.analyzerPlugin.clasifiers;

/**
 * Created by marodrig on 10/10/2014.
 */
public class TagedWeak extends TagedTransform {
    public TagedWeak() {
        tagClassification = "weak";
    }


    @Override
    public int getWeight() {
        return WEAK;
    }
}
