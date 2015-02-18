package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class HitOrder implements Order {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        long k = o2.getHits() - o1.getHits();
        if (k == 0) return 0;
        return k < 0 ? -1 : 1;
    }

    @Override
    public String getDescription() {
        return "Sort by hits";
    }
}
