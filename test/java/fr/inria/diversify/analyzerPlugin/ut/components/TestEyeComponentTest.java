package fr.inria.diversify.analyzerPlugin.ut.components;

import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import org.junit.Test;

/**
 * Created by marodrig on 28/01/2015.
 */
public class TestEyeComponentTest {

    /**
     * Test the creation of the program... for real
     */
    @Test
    public void testCreateProgramForReal() {
        TestEyeProjectComponent component = new TestEyeProjectComponent(new FakeProject());

    }

}
