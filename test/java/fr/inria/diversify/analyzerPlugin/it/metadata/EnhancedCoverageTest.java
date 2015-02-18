package fr.inria.diversify.analyzerPlugin.it.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageReader;
import org.junit.Test;

import java.util.ArrayList;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfos;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 04/02/2015.
 */
public class EnhancedCoverageTest {

    /**
     * Test one file
     * @throws Exception
     */
    @Test
    public void testSimpleRead() throws Exception {
        ArrayList<TransformationInfo> infos = createInfos();

        EnhancedCoverageReader reader = new EnhancedCoverageReader(infos);
        reader.read("idFile.id", "test/data/enhancedCoverageLogFiles/simple");

        assertEquals(4, infos.get(0).getHits());
        assertEquals(1, infos.get(1).getHits());

        assertEquals(1, infos.get(0).getTests().size());
        assertEquals(1, infos.get(1).getTests().size());

        assertEquals(2, infos.get(0).getAsserts().size());
        assertEquals(1, infos.get(1).getAsserts().size());

        assertEquals(1, infos.get(1).getTotalAssertionHits());
        assertEquals(6, infos.get(0).getTotalAssertionHits());
    }

    /**
     * Test multi-reading file
     * @throws Exception
     */
    @Test
    public void testMultiThreadRead() throws Exception {
        ArrayList<TransformationInfo> infos = createInfos();

        EnhancedCoverageReader reader = new EnhancedCoverageReader(infos);
        reader.read("idFile.id", "test/data/enhancedCoverageLogFiles/multi");

        assertEquals(5, infos.get(0).getHits());
        assertEquals(2, infos.get(1).getHits());

    }

}
