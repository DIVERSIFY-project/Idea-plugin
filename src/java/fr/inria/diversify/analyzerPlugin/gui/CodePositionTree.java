package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

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
public class CodePositionTree extends Tree {

    public static final DataKey<CodePositionTree>
            TEST_EYE_CODE_POSITION_TREE = DataKey.create("test.eye.code.position.tree");

    private IDEObjects ideObjects;

    public CodePositionTree() {
        super(getDefaultTreeModel());
        init();
    }

    public CodePositionTree(TreeNode root) {
        super(new DefaultTreeModel(root, false));
        init();
    }

    public CodePositionTree(TreeModel treemodel) {
        super(treemodel);
        init();
    }

    protected void init() {
        setModel(null);
        setToggleClickCount(0);
        setRootVisible(false);
        installDoubleClick();
    }

    /**
     * Installs the double click
     */
    protected void installDoubleClick() {
        final CodePositionTree me = this;
        final MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ActionManager.getInstance().tryToExecute(
                            new SeekCodeTransformation(me), e, e.getComponent(), null, true);
                }
            }
        };
        addMouseListener(listener);
    }

    /**
     * Gets the user object (CodePosition) of the selected component of a tree
     *
     * @return A CodePosition object contained in the node
     */
    public CodePosition getSelectedCodePosition() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                getLastSelectedPathComponent();
        return node == null ? null : (CodePosition) node.getUserObject();
    }

    public IDEObjects getIDEObjects() {
        return ideObjects;
    }

    public void setIDEObjects(IDEObjects ideObjects) {
        this.ideObjects = ideObjects;
    }
}
