package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
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
    private final JTree tree;

    /**
     * Label to show displayed totals
     */
    private final JLabel label;

    public ShowTransformationsInTree(JTree tree, JLabel label) {
        super();
        this.tree = tree;
        this.label = label;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestEyeProjectComponent p = getComponent(e);

        int tpCount = 0;
        int tCount = 0;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Transformations");
        DefaultTreeModel model = new DefaultTreeModel(root);

        for (TransformationInfo tp : p.getVisibleInfos()) {

            DefaultMutableTreeNode rep = new DefaultMutableTreeNode(tp);
            for (TransplantInfo t : tp.getTransplants()) {
                if (t.getVisibility() == TransplantInfo.Visibility.show) {
                    tCount++;
                    rep.insert(new DefaultMutableTreeNode(t), rep.getChildCount());
                }
            }
            if (rep.getChildCount() > 0) {
                tpCount++;
                model.insertNodeInto(rep, root, root.getChildCount());
            }
        }
        tree.setModel(model);
        if ( label != null )
            label.setText("Transformations: " + tCount + " | " + "Pots: " + tpCount);
    }
}
