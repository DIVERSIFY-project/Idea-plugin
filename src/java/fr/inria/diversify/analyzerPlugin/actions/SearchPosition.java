package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

/**
 * Created by marodrig on 21/12/2014.
 */
public class SearchPosition extends WinAction {

    private int direction = 1;

    public SearchPosition(MainToolWin mainToolWin) {
        super(mainToolWin);
    }

    public SearchPosition(MainToolWin me, int i) {
        super(me);
        direction = i;
    }

    private void selectNode(JTree tree, TreeNode searchNode) {
        TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(searchNode);
        TreePath tpath = new TreePath(nodes);
        tree.scrollPathToVisible(tpath);
        tree.setSelectionPath(tpath);
    }

    public CodePosition getSelectedCP(JTree tree) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        return node == null ? null : (CodePosition) node.getUserObject();
    }

    @Override
    public void execute() {
        JTree t = getMainToolWin().getTreeTransformations();
        //selectNode(t, (TreeNode)t.getModel().getRoot());
        String pos = getMainToolWin().getTextSearch().getText().toLowerCase(); //search string
        CodePosition c = getSelectedCP(t); //stop condition
        Enumeration e = ((DefaultMutableTreeNode) t.getModel().getRoot()).preorderEnumeration();

        //find previous if the current selected element contains the string in the position
        if (c != null ) {
            DefaultMutableTreeNode prev = null;
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
                if (n.getUserObject() instanceof CodePosition) {
                    CodePosition cp = (CodePosition) n.getUserObject();
                    if (cp.equals(c)) {
                        if (direction < 0) {
                            if (prev != null) selectNode(t, prev);
                            else selectNode(t, n);
                            return; //found let's go!!!
                        }
                        break;
                    }
                    //Find the previous
                    if (n.toString().toLowerCase().contains(pos)) prev = n;
                }
            }
        }

        //Find next
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
            if (n.toString().toLowerCase().contains(pos)) {
                selectNode(t, n);
                return;
            }
        }
    }
}
