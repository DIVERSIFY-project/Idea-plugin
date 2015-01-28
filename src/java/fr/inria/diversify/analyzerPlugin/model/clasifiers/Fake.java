package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

/**
 * Created by marodrig on 27/10/2014.
 */
public class Fake extends TransformClasifier {
    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        if (transform.getType().contains("replace")) {
            return transform.getSource().equals(transform.getTransplantationPoint().getSource());
        }
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return getWeight();
    }

    @Override
    public String getDescription() {
        return "Fake replace (TP == Transplant)";
    }

    @Override
    public int getWeight() {
        return WEAK;
    }
}
