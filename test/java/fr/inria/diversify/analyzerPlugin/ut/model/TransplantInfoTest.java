package fr.inria.diversify.analyzerPlugin.ut.model;

import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 08/10/2014.
 */
public class TransplantInfoTest {

    @Ignore
    @Test
    public void testToJson() {

    }

    /**
     * Test the proper calculation of the classification strength
     */
    @Test
    public void testStrength() {
        TransplantInfo p = new TransplantInfo();
        p.setClassification("F1", 5);
        p.setClassification("F2", -15);
        assertTrue(p.strength() + 10.0 < 0.00000000001);

        p = new TransplantInfo();
        p.setClassification("F1", 15);
        p.setClassification("F2", -15);
        assertTrue(p.strength() < 0.00000000001);
    }


    /**
     * Test the proper calculation of the classification strength
     */
    @Test
    public void testClasifications() {
        TransplantInfo p = new TransplantInfo();
        p.setClassification("F1", 5);
        p.setClassification("F2", -15);
        p.setClassification("F3", 0);
        assertEquals(p.getClassifications(), "F1, F2, ");
    }

}
