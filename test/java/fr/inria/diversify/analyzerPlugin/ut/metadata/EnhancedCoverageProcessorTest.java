package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageEntry;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import fr.inria.diversify.syringe.processor.EntryLog;
import fr.inria.diversify.syringe.processor.LoadingException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfos;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 04/02/2015.
 */
public class EnhancedCoverageProcessorTest {

    public static List<EntryLog> getLogs() throws LoadingException {
        HashMap<Integer, String> m = new HashMap<>();
        m.put(1, "<CtMethodImpl>TestMethod1:1");
        m.put(2, "<CtMethodImpl>TestMethod2:2");
        m.put(3, "<CtInvocationImpl>assert1:10");
        m.put(4, "<CtInvocationImpl>assert2:100");
        m.put(5, "<CtInvocationImpl>org.MyOtherClass:100");
        m.put(6, "<CtInvocationImpl>org.MyOtherClass:10");

        ArrayList<EntryLog> result = new ArrayList<>();
        EnhancedCoverageEntry e;
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("TB;12345678901;1", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("T;12345678902;5;8", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("AS;12345678903;3", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("AS;12345678904;4", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("T;12345678908;6;6", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("AS;123456789010;4", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("TC;123456789012;5;3;6;7;8", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("ASC;123456789012;3;4", ";");
        e = new EnhancedCoverageEntry("My File",1, m);result.add(e);e.fromLine("TE;123456789013;", ";");
        return result;
    }

    /**
     * Test one file kind-o situation
     * @throws Exception
     */
    @Test
    public void testSimpleRead() throws Exception {
        ArrayList<TransformationInfo> infos = createInfos();

        EnhancedCoverageProcessor p = new EnhancedCoverageProcessor(infos);
        p.process(getLogs());

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
     * Test multi-reading file kind-o situation
     * @throws Exception
     */
    @Test
    public void testMultiThreadRead() throws Exception {
        ArrayList<TransformationInfo> infos = createInfos();

        EnhancedCoverageProcessor p = new EnhancedCoverageProcessor(infos);
        List<EntryLog> logs = getLogs();
        EnhancedCoverageEntry e;
        e = new EnhancedCoverageEntry("My File",1, logs.get(0).getIdMap());logs.add(e);e.fromLine("T;12345678902;5;8", ";");
        e = new EnhancedCoverageEntry("My File",1, logs.get(0).getIdMap());logs.add(e);e.fromLine("T;12345678902;6;8", ";");
        p.process(logs);

        assertEquals(5, infos.get(0).getHits());
        assertEquals(2, infos.get(1).getHits());
    }

}
