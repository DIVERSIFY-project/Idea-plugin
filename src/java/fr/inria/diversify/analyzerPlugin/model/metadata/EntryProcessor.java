package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.LoadingException;

import java.util.Collection;
import java.util.List;

/**
 * Created by marodrig on 04/02/2015.
 */
public interface EntryProcessor {

    public void process(Collection<EntryLog> entries) throws LoadingException;

    public List<String> getErrors();

}
