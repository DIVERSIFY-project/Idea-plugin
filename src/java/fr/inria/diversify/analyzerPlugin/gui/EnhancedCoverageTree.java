package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by marodrig on 03/02/2015.
 */
public class EnhancedCoverageTree extends CodePositionTree implements com.intellij.openapi.actionSystem.DataProvider {

    public static final DataKey<EnhancedCoverageTree>
            ENHANCED_COVERAGE_TREE_DATA_KEY = DataKey.create("test.eye.tree.coverage");

    /**
     * Number of asserts in the tree
     */
    private int assertCount;

    /**
     * Number of test in the tree
     */
    private int testCount;


    public EnhancedCoverageTree() {
        super((TreeModel) getDefaultTreeModel());
        init();
    }

    public EnhancedCoverageTree(TreeNode root) {
        super((TreeModel) (new DefaultTreeModel(root, false)));
        init();
    }

    public EnhancedCoverageTree(TreeModel treemodel) {
        super(treemodel);
        init();
    }


    @Override
    protected void init() {
        super.init();
        setCellRenderer(new EnhancedCoverageNodeRenderer());
    }

    @Nullable
    @Override
    public Object getData(String s) {
        return s.equals(ENHANCED_COVERAGE_TREE_DATA_KEY.getName()) ? this : null;
    }

    /**
     * Shows the coverage info of the given transformation info
     *
     * @param info
     */
    public void showCoverage(TransformationInfo info) {

        setModel(null);
        if (info.getTests() == null) return;


        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Coverage");
        DefaultTreeModel model = new DefaultTreeModel(root);

        assertCount = 0;
        testCount = 0;
        for (TestInfo t : info.getTests().keySet()) {
            testCount++;
            DefaultMutableTreeNode testNode = new DefaultMutableTreeNode(t);
            root.insert(testNode, root.getChildCount());
            if ( t.getAsserts() == null ) continue;
            for (AssertInfo a : t.getAsserts()) {
                assertCount++;
                testNode.insert(new DefaultMutableTreeNode(a), testNode.getChildCount());
            }
        }
        setModel(model);
    }

    public int getTestCount() {
        return testCount;
    }

    public int getAssertCount() {
        return assertCount;
    }
}
