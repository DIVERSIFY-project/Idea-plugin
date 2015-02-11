package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.*;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowCoverageInfo;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadEnhancedCoverageAction;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.EnhancedCoverageTree;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageReader;
import fr.inria.diversify.analyzerPlugin.model.metadata.SyringeDataReader;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.io.FileNotFoundException;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.assertActionCalled;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.expectHardComplain;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.verifyHardComplain;
import static fr.inria.diversify.analyzerPlugin.ut.actions.LoadTransformationsActionTest.buildActionManager;
import static fr.inria.diversify.analyzerPlugin.ut.actions.LoadTransformationsActionTest.expectFileChooser;
import static fr.inria.diversify.analyzerPlugin.ut.actions.LoadTransformationsActionTest.verifyFileChooser;
import static junit.framework.TestCase.assertEquals;

/**
 * Test class for the LoadTransformationsAction
 * <p/>
 * Created by marodrig on 26/01/2015.
 */
@RunWith(JMockit.class)
public class LoadEnhancedCoverageActionTest {

    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testLoadTransformations(@Mocked final FileChooser anyChooser,
                                        @Mocked final EnhancedCoverageReader anyReader)
            throws FileNotFoundException, LoadingException {

        expectFileChooser();
        new Expectations() {{
            anyReader.read(anyString, anyString);
        }};

        //Register the post action
        LoadEnhancedCoverageAction action = new LoadEnhancedCoverageAction();
        action.setIdeObjects(new FakeIDEObjects());
        action.actionPerformed(new FakeAnActionEvent(
                buildActionManager(new ShowCoverageInfo(new CodePositionTree(), new EnhancedCoverageTree()))));

        verifyFileChooser();

        new Verifications() {{
            anyReader.read(anyString, anyString);
        }};

        //Verify post action was called
        assertActionCalled(action, ShowCoverageInfo.class, 1);
    }

    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testComplain(@Mocked final FileChooser anyChooser,
                             @Mocked final JOptionPane anyPane) {
        expectHardComplain();
        //Will try to do it's thing without proper environment
        LoadEnhancedCoverageAction action = new LoadEnhancedCoverageAction();
        action.actionPerformed(null);
        //Verify tht a complain action was called
        verifyHardComplain();
    }

}