package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.*;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowCoverageInfo;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadEnhancedCoverageAction;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.EnhancedCoverageTree;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.expectHardComplain;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.getInfos;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.verifyHardComplain;
import static fr.inria.diversify.analyzerPlugin.ut.metadata.EnhancedCoverageProcessorTest.getLogs;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 04/02/2015.
 */
@RunWith(JMockit.class)
public class ShowEnhancedCoverageActionTest {

    /**
     * Test the perform action of the ShowEnhancedCoverageAction
     * @throws LoadingException
     */
    @Test
    public void testPerformAction() throws LoadingException {
        ArrayList<TransformationInfo> infos = getInfos();
        EnhancedCoverageProcessor p = new EnhancedCoverageProcessor(infos);
        p.process(getLogs());

        //The Fake Code position tree always return as selected code position the FakeCodePosition property
        FakeCodePositionTree tree = new FakeCodePositionTree();
        tree.setFakeCodePosition(infos.get(0));

        EnhancedCoverageTree covTree = new EnhancedCoverageTree();

        ShowCoverageInfo action = new ShowCoverageInfo(tree, covTree);
        action.setIdeObjects(new FakeIDEObjects());
        //We don't use the component
        action.actionPerformed(null);

        DefaultMutableTreeNode r = (DefaultMutableTreeNode) covTree.getModel().getRoot();
        assertEquals(1, r.getChildCount());
        assertEquals(2, r.getChildAt(0).getChildCount());

        assertEquals(1, covTree.getTestCount());
        assertEquals(2, covTree.getAssertCount());
    }


    /**
     * Test the proper loading of the transformations.
     */
    @Test
    public void testComplain(@Mocked final FileChooser anyChooser,
                             @Mocked final JOptionPane anyPane) {
        expectHardComplain();
        //Will try to do it's thing without proper environment
        ShowCoverageInfo action = new ShowCoverageInfo(null, null); //<-This will make it complain
        action.actionPerformed(null);
        //Verify tht a complain action was called
        verifyHardComplain();
    }
}

