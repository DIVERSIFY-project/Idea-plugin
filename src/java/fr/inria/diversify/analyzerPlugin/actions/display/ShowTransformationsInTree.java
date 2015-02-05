package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Action to show the set of visible TransformationInfos in the project to a JTree
 *
 * Created by marodrig on 26/01/2015.
 */
public class ShowTransformationsInTree extends TestEyeAction {

    /**
     * Tree that we want to put infos in
     */
    private final TreeTransformations tree;

    /**
     * Label to show displayed totals
     */
    private final JLabel label;

    public ShowTransformationsInTree(TreeTransformations tree, JLabel label) {
        super();
        this.tree = tree;
        this.label = label;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestEyeProjectComponent p = getComponent(e);
        tree.setInfos(p.getVisibleInfos());
        if ( label != null )
            label.setText("Transformations: " + tree.getTransplantCount() + " | " + "Pots: " + tree.getPotCount());
        tryExecute(EnableDisableFilterPanel.class, e);
    }
}
