package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageEntry;
import fr.inria.diversify.syringe.processor.LoadingException;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 04/02/2015.
 */
public class EnhancedCoverageEntryLogTest {

    private HashMap<Integer, String> testMap() {
        HashMap<Integer, String> m = new HashMap<>();
        m.put(1, "Pos:1");
        m.put(2, "Pos:2");
        return m;
    }

    @Test
    public void tesdtFromLine_TP() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("T;12345678902;1;8", ";");
        assertEquals(12345678902L, e.getMillis());
        assertEquals("Pos:1", e.getPosition());
        assertEquals(8, e.getMeanDepth());
    }

    @Test
    public void testFromLine_TestBegin() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("TB;12345678901;2", ";");
        assertEquals("TB", e.getType());
        assertEquals(12345678901L, e.getMillis());
        assertEquals("Pos:2", e.getPosition());
    }

    @Test
    public void testFromLine_TestEnd() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("TE;12345678901", ";");
        assertEquals("TE", e.getType());
        assertEquals(12345678901L, e.getMillis());
    }

    @Test
    public void testFromLine_TPCount() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("TC;123456789012;2;3;6;7;8", ";");
        assertEquals("TC", e.getType());
        assertEquals(123456789012L, e.getMillis());
        assertEquals("Pos:2", e.getPosition());
        assertEquals(3, e.getExecutions());
        assertEquals(6, e.getMinDepth());
        assertEquals(7, e.getMeanDepth());
        assertEquals(8, e.getMaxDepth());
    }

    @Test
    public void testFromLine_AssertCount() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("ASC;123456789012;1;4", ";");
        assertEquals("ASC", e.getType());
        assertEquals(123456789012L, e.getMillis());
        assertEquals("Pos:1", e.getPosition());
        assertEquals(4, e.getExecutions());
    }

    @Test
    public void testFromLine_Assert() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("AS;123456789010;1", ";");
        assertEquals("AS", e.getType());
        assertEquals(123456789010L, e.getMillis());
        assertEquals("Pos:1", e.getPosition());
    }

    @Test(expected = LoadingException.class)
    public void testFromLine_Exception() throws LoadingException {
        EnhancedCoverageEntry e = new EnhancedCoverageEntry("My File", 1, testMap());
        e.fromLine("AS;123456789010;10", ";");
    }
}
