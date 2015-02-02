package fr.inria.diversify.analyzerPlugin.model.comparators;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class VarDiffOrder implements Comparator<TransformationInfo> {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return o2.getVarDiff() - o1.getVarDiff();
    }
}
