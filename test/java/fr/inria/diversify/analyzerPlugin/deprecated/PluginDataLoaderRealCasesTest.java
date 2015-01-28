package fr.inria.diversify.analyzerPlugin.deprecated;

import fr.inria.diversify.analyzerPlugin.model.io.PluginDataLoader;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by marodrig on 22/12/2014.
 */
public class PluginDataLoaderRealCasesTest {

    private String getResourcePath(String name) throws Exception {
        return getClass().getResource("/" + name).toURI().getPath();
    }

    /**
     * A unit test to load easy mock's augmented metrics
     */
    @Test
    @Ignore
    public void easyMockAugmentedMetrics() throws Exception {
        PluginDataLoader formatter = new PluginDataLoader();
        Collection<TransformationInfo> representations =
                formatter.fromScattered(getResourcePath("easymock3.2-non-rep-index.json"),
                        getResourcePath("easymock-AugmentedMetricsLOG"));

    }

    /**
     * A unit test to load easy mock's augmented metrics
     */
    @Test
    @Ignore
    public void commonCollAugmentedMetrics() throws Exception {
        PluginDataLoader formatter = new PluginDataLoader();
        Collection<TransformationInfo> representations =
                formatter.fromScattered(getResourcePath("commons-collections_corrected.json"),
                        getResourcePath("collections-AugmentedMetricsLOG"));
        System.out.print(formatter.getErrors().size());
    }
}
