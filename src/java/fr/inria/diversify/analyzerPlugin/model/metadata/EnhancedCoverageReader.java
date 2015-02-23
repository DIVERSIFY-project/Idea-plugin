package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.syringe.processor.SyringeDataReader;

import java.util.Collection;

/**
 * Created by marodrig on 04/02/2015.
 */
public class EnhancedCoverageReader extends SyringeDataReader {
    public EnhancedCoverageReader(Collection<TransformationInfo> infos) {
        super(new EnhancedCoverageEntryFactory(), new EnhancedCoverageProcessor(infos));
    }
}
