package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.JBLabel;
import fr.inria.diversify.analyzerPlugin.FakeAnActionEvent;
import fr.inria.diversify.analyzerPlugin.FakeIDEObjects;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.display.EnableDisableFilterPanel;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.ut.MockInputProgram;
import com.intellij.ui.treeStructure.Tree;
import org.junit.Test;

import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.assertActionCalled;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 27/01/2015.
 */
public class ShowTransformationsInTreeTest {

    /**
     * Produces a fake action event that will feed the action with proper data
     * @return
     */
    private AnActionEvent getActionEvent() {
        //Create the transformations
        List<TransformationInfo> infos  = new ArrayList<>(
                TransformationInfo.fromTransformations(createTransformations(new MockInputProgram())));
        //Make a fake project
        FakeProject f = new FakeProject();
        f.getComponent(TestEyeProjectComponent.class).setVisibleInfos(infos);
        return new FakeAnActionEvent(f);
    }

    /**
     * Test that a set of tranformation infos are properly displayed in a tree
     */
    @Test
    public void testShowInJTree() {
        Tree tree = new Tree();
        ShowTransformationsInTree t = new ShowTransformationsInTree(tree, new JBLabel());
        t.setIdeObjects(new FakeIDEObjects());
        t.actionPerformed(getActionEvent());
        assertShowingTransformationsInTree(tree);
        assertActionCalled(t, EnableDisableFilterPanel.class, 1);
    }

    /**
     * Test that a set of tranformation are properly totaled in the totals label
     */
    @Test
    public void testShowInTotalBottomLabel() {
        JBLabel label = new JBLabel();
        ShowTransformationsInTree t = new ShowTransformationsInTree(new Tree(), label);
        t.setIdeObjects(new FakeIDEObjects());
        t.actionPerformed(getActionEvent());
        assertEquals("Transformations: 4 | Pots: 2", label.getText());
        assertActionCalled(t, EnableDisableFilterPanel.class, 1);
    }

    public static void assertShowingTransformationsInTree(Tree tree) {
        //Test that the transformations are in the tree
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        assertEquals(2, model.getChildCount(model.getRoot()));
        assertEquals(3, model.getChildCount(model.getChild(model.getRoot(), 0)));
    }

}
