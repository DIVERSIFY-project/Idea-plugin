package fr.inria.diversify.analyzerPlugin.clasifiers;

import fr.inria.diversify.analyzerPlugin.Transplant;

/**
 * Gives a little high weight to all delete transformations
 *
 * Created by marodrig on 13/10/2014.
 */
public class DeleteSubstitution extends TransformClasifier {
    @Override
    protected boolean canClassify(Transplant transform) {
        return transform.getType().equals("delete");
    }

    @Override
    protected int calculateValue(Transplant transform) {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Delete transformations";
    }
}
