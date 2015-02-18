package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class TotalTransplantsOrder implements Order {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return o2.getTransplants().size() - o1.getTransplants().size();
    }

    @Override
    public String getDescription() {
        return "Sort by number of transplants";
    }
}
