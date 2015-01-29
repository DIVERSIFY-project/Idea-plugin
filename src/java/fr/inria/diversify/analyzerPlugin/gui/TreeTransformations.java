package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Created by marodrig on 29/01/2015.
 */
public class TreeTransformations extends Tree {

    public TreeTransformations() {
        super((TreeModel) getDefaultTreeModel());
    }

    public TreeTransformations(TreeNode root) {
        super((TreeModel) (new DefaultTreeModel(root, false)));
    }

    public TreeTransformations(TreeModel treemodel) {
        super(treemodel);
    }

}
