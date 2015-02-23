package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.syringe.processor.EntryFactory;
import fr.inria.diversify.syringe.processor.EntryLog;

import java.util.HashMap;

/**
 * Created by marodrig on 04/02/2015.
 */
public class EnhancedCoverageEntryFactory extends EntryFactory {
    @Override
    public EntryLog build(String file, int line, HashMap<Integer, String> idMap) {
        return new EnhancedCoverageEntry(file, line, idMap);
    }
}
