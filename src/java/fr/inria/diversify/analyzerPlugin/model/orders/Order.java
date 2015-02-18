package fr.inria.diversify.analyzerPlugin.model.orders;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;

import java.util.Comparator;

/**
 * Created by marodrig on 14/02/2015.
 */
public interface Order extends Comparator<TransformationInfo> {

    public String getDescription();

}
