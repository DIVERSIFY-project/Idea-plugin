package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.Transplant;

/**
 * Gives a little high weight to all delete transformations
 * <p/>
 * Created by marodrig on 13/10/2014.
 */
public class DeleteSubstitution extends TransformClasifier {

    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(Transplant transform) {
        return transform.getType().equals("delete");
    }

    @Override
    protected int calculateValue(Transplant transform) {
        return getWeight();
    }

    @Override
    public String getDescription() {
        return "Delete transformations";
    }

    @Override
    public int getWeight() {
        return MEDIUM;
    }
}
