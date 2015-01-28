package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.*;
import fr.inria.diversify.analyzerPlugin.actions.ComplainAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadTransformationsAction;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;
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

import static fr.inria.diversify.analyzerPlugin.ut.actions.ShowTransformationsInTreeTest.assertShowingTransformationsInTree;
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
     * A stub complain action
     */
    class MyComplainAction extends AnAction {
        public int callCount = 0;

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            callCount++;
        }
    }

    public static ActionManager buildActionManager(Class<?> c, AnAction anAction) {
        ActionManager m = new FakeActionManager();
        m.registerAction(TestEyeExplorer.PLUG_NAME_PREFIX + c.getSimpleName(), anAction);
        return m;
    }

    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testLoadTransformations(@Mocked final FileChooser anyChooser) {
        new Expectations() {{
            //Mocks the IntelliJ idea API...
            FileChooser.chooseFile(
                    withInstanceOf(FileChooserDescriptor.class), null, null);
            result = new MyFakeVirtualFile(); //Returns a fixed path
        }};

        //Register the post action
        Tree tree = new Tree();
        tree.setModel(null);
        ShowTransformationsInTree st = new ShowTransformationsInTree(tree, new JBLabel());

        //Load the transformations and then call the post action
        LoadTransformationsAction action = new MockLoadTransformationsAction();
        action.actionPerformed(new FakeAnActionEvent(buildActionManager(st.getClass(), st)));
        //Verify the file chooser was called
        new Verifications() {{
            FileChooser.chooseFile(withInstanceOf(FileChooserDescriptor.class), null, null);
            times = 1;
        }};
        //Verify post action was called
        assertShowingTransformationsInTree(tree);
    }

    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testComplain(@Mocked final FileChooser anyChooser,
                             @Mocked final JOptionPane anyPane) {
        new Expectations() {{
            JOptionPane.showMessageDialog(null, anyString, anyString, JOptionPane.ERROR_MESSAGE);
        }};

        new Expectations() {{
            //Mocks the IntelliJ idea API...
            FileChooser.chooseFile(
                    withInstanceOf(FileChooserDescriptor.class), null, null);
            result = new MyFakeVirtualFile(); //Returns a fixed path
        }};

        MyComplainAction complain = new MyComplainAction();
        //Will try to do it's thing without proper environment
        LoadTransformationsAction action = new LoadTransformationsAction();
        action.actionPerformed(new FakeAnActionEvent(buildActionManager(ComplainAction.class, complain)));

        //Verify tht a complain action was called
        new Verifications() {{
            JOptionPane.showMessageDialog(null, anyString, anyString, JOptionPane.ERROR_MESSAGE);
        }};
    }

}
