package fr.inria.diversify.analyzerPlugin.model.comparators;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class TestsOrder implements Comparator<TransformationInfo> {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return o2.getTests().size() - o1.getTests().size();
    }
}
