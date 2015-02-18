package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class VarDiffOrder implements Order {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return o2.getVarDiff() - o1.getVarDiff();
    }

    @Override
    public String getDescription() {
        return "Sort by variable differences";
    }
}
