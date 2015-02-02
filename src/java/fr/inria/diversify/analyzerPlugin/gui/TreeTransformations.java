package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.actions.searching.SwitchClasifierAction;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by marodrig on 29/01/2015.
 */
public class TreeTransformations extends Tree implements com.intellij.openapi.actionSystem.DataProvider {

    private IDEObjects ideObjects;

    public static final DataKey<TreeTransformations>
            TEST_EYE_TREE_TRANSFORMATIONS = DataKey.create("test.eye.tree.transformations");


    public TreeTransformations() {
        super((TreeModel) getDefaultTreeModel());
        init();
    }

    public TreeTransformations(TreeNode root) {
        super((TreeModel) (new DefaultTreeModel(root, false)));
        init();
    }

    public TreeTransformations(TreeModel treemodel) {
        super(treemodel);
        init();
    }

    private void init() {
        setModel(null);
        setToggleClickCount(0);
        setRootVisible(false);
        installPopup();
        installDoubleClick();
    }

    /**
     * Installs the double click
     */
    private void installDoubleClick() {

        final Tree me = this;
        final MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ActionManager.getInstance().tryToExecute(new SeekCodeTransformation(), e, me, null, true);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ActionManager m = ActionManager.getInstance();
                m.tryToExecute(m.getAction(ShowTransformationProperties.ID), e, me, null, true);
            }
        };

        addMouseListener(listener);
    }

    /**
     * Installs the popup
     */
    private void installPopup() {
        final PopupHandler popupHandler = new PopupHandler() {
            public void invokePopup(Component comp, int x, int y) {

                final DefaultActionGroup popupGroup = new DefaultActionGroup();
                popupGroup.add(new SeekCodeTransformation());
                ActionPopupMenu popupMenu = ideObjects.getActionManager().createActionPopupMenu(
                        TreeTransformations.class.getName(), popupGroup);
                if (popupMenu != null) {
                    popupMenu.getComponent().show(comp, x, y);
                }
            }
        };
        addMouseListener(popupHandler);
    }

    @Nullable
    @Override
    public Object getData(String s) {
        return s.equals(TEST_EYE_TREE_TRANSFORMATIONS.getName()) ? this : null;
    }

    public IDEObjects getIDEObjects() {
        return ideObjects;
    }

    public void setIDEObjects(IDEObjects ideObjects) {
        this.ideObjects = ideObjects;
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
}
