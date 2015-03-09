package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

/**
 * Created by marodrig on 02/02/2015.
 */
public class Strength implements Order {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {

        return Math.round(o2.strength() - o1.strength());
    }

    @Override
    public String getDescription() {
        return "Strength";
    }
}
