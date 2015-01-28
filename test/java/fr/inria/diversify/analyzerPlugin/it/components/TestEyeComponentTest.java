package fr.inria.diversify.analyzerPlugin.it.components;

import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.diversification.InputProgram;
import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 28/01/2015.
 */
public class TestEyeComponentTest {

    /**
     * Test the creation of the program. Uses data from the data folder
     */
    @Test
    public void testCreateProgram() {
        Project p = new FakeProject();
        TestEyeProjectComponent component = new TestEyeProjectComponent(p);
        InputProgram inputProgram = component.getProgram();
        assertNotNull(inputProgram);
        assertEquals(p.getBasePath() + "\\src\\main\\java", inputProgram.getSourceCodeDir());
    }

}
