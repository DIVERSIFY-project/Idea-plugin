package fr.inria.diversify.analyzerPlugin.model.metadata;

import java.util.HashMap;

/**
 * A factory to build entries
 */
public abstract class EntryFactory {
    abstract EntryLog build(String file, int line, HashMap<Integer, String> idMap);
}
