package fr.inria.diversify.analyzerPlugin.gui;

import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import icons.TestEyeIcons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by marodrig on 03/02/2015.
 */
public class EnhancedCoverageNodeRenderer extends DefaultTreeCellRenderer {


    public EnhancedCoverageNodeRenderer() {
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        setFont(getFont().deriveFont(Font.PLAIN));

        if ( value instanceof DefaultMutableTreeNode) {
            if ( ((DefaultMutableTreeNode)value).getUserObject() instanceof TestInfo) {
                setIcon(TestEyeIcons.Test);
            } else if  ( ((DefaultMutableTreeNode)value).getUserObject() instanceof AssertInfo) {
                setIcon(TestEyeIcons.Assert);
            }
        }

        return this;
    }
}
