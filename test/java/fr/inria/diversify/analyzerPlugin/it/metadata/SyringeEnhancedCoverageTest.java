package fr.inria.diversify.analyzerPlugin.it.metadata;

import fr.inria.diversify.analyzerPlugin.LoadingException;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageEntryFactory;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import fr.inria.diversify.analyzerPlugin.model.metadata.SyringeDataReader;
import fr.inria.diversify.ut.MockInputProgram;
import org.junit.Test;
import java.util.ArrayList;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.getInfos;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 04/02/2015.
 */
public class SyringeEnhancedCoverageTest {

    /**
     * Test one file
     * @throws Exception
     */
    @Test
    public void testSimpleRead() throws Exception {
        ArrayList<TransformationInfo> infos = getInfos();

        SyringeDataReader syringe = new SyringeDataReader(
                new EnhancedCoverageEntryFactory(), new EnhancedCoverageProcessor(infos));
        syringe.read("idFile.id", "test/data/enhancedCoverageLogFiles/simple");

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
        ArrayList<TransformationInfo> infos = getInfos();
        SyringeDataReader syringe = new SyringeDataReader(
                new EnhancedCoverageEntryFactory(), new EnhancedCoverageProcessor(infos));
        syringe.read("idFile.id", "test/data/enhancedCoverageLogFiles/multi");

        assertEquals(5, infos.get(0).getHits());
        assertEquals(2, infos.get(1).getHits());

    }

    /**
     * Test multi-reading file
     * @throws Exception
     */
    @Test(expected = LoadingException.class)
    public void testLoadException() throws Exception {
        ArrayList<TransformationInfo> infos = getInfos();
        SyringeDataReader syringe = new SyringeDataReader(
                new EnhancedCoverageEntryFactory(), new EnhancedCoverageProcessor(infos));
        syringe.read("idFile.id", "test/data/enhancedCoverageLogFiles/error");
    }

}
