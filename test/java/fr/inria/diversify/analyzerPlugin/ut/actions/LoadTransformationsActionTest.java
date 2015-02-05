package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.*;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadTransformationsAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponentImpl;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.ut.MockInputProgram;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.assertActionCalled;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.expectHardComplain;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.verifyHardComplain;
import static junit.framework.TestCase.assertEquals;

/**
 * Test class for the LoadTransformationsAction
 * <p/>
 * Created by marodrig on 26/01/2015.
 */
@RunWith(JMockit.class)
public class LoadTransformationsActionTest {

    /**
     * Mocks the LoadTransformationsAction so the factory method getReader returns a ByteInputStream
     */
    public class MockLoadTransformationsAction extends LoadTransformationsAction {
        @Override
        protected InputStreamReader getReader(String streamPath) {
            assertEquals(MyFakeVirtualFile.ABSOLUTE_PATH, streamPath);
            return new InputStreamReader(
                    new ByteArrayInputStream(TestHelpers.createTransformationsJSON(
                            new MockInputProgram()).toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    /**
     * Builds a fake action manager with a set of actions inside
     * @param actions
     * @return
     */
    public static ActionManager buildActionManager(AnAction... actions) {
        ActionManager m = new FakeActionManager();
        for (AnAction a : actions)
            m.registerAction(TestEyeApplicationComponentImpl.PLUG_NAME_PREFIX + a.getClass().getSimpleName(), a);
        return m;
    }

    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testLoadTransformations(@Mocked final FileChooser anyChooser) {

        //Mocks the IntelliJ idea API File chooser...
        expectFileChooser();

        //Register the post action to be called after the LoadTransformations
        TreeTransformations tree = new TreeTransformations();
        tree.setModel(null);
        ShowTransformationsInTree st = new ShowTransformationsInTree(tree, new JBLabel());

        //Load the transformations and call the post action
        LoadTransformationsAction action = new MockLoadTransformationsAction();
        action.setIdeObjects(new FakeIDEObjects());
        action.actionPerformed(new FakeAnActionEvent(buildActionManager(st)));

        //Verify the file chooser was called
        verifyFileChooser();
        //Verify post action was called
        assertActionCalled(action, ShowTransformationsInTree.class, 1);
    }

    /**
     * Verify the file chooser was called
     */
    public static void verifyFileChooser() {
        new Verifications() {{
            FileChooser.chooseFile(withInstanceOf(FileChooserDescriptor.class), null, null);
            times = 1;
        }};
    }

    /**
     * Mocks the IntelliJ idea API File chooser...
     */
    public static void expectFileChooser() {
        new Expectations() {{
            FileChooser.chooseFile(withInstanceOf(FileChooserDescriptor.class), null, null);
            result = new MyFakeVirtualFile(); //Returns a fixed path
        }};
    }


    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testComplain(@Mocked final FileChooser anyChooser,
                             @Mocked final JOptionPane anyPane) {

        expectHardComplain();

        //Will try to do it's thing without proper environment
        LoadTransformationsAction action = new LoadTransformationsAction();
        action.actionPerformed(null); //<-This will certainly make it complain

        verifyHardComplain();
    }




}
