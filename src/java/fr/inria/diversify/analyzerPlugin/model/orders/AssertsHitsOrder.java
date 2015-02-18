package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class AssertsHitsOrder implements Order {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return (int) Math.signum((double) (o2.getTotalAssertionHits() - o1.getTotalAssertionHits()));
    }


    @Override
    public String getDescription() {
        return "Sort by assert hits";
    }
}
