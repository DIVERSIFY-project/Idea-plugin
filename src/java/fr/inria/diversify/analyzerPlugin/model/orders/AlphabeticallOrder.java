package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 02/02/2015.
 */
public class AlphabeticallOrder implements Comparator<TransformationInfo> {
    @Override
    public int compare(TransformationInfo o1, TransformationInfo o2) {
        return (o1.toString().compareTo(o2.toString()));
    }
}