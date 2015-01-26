package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by marodrig on 23/09/2014.
 */
public class TransplantNodeRenderer extends DefaultTreeCellRenderer {

    private final Icon delete;
    private final Icon replace;
    private final Icon add;
    private final Icon iconTP;

    public TransplantNodeRenderer(Icon add, Icon replace, Icon delete, Icon tp) {
        this.add = add;
        this.replace = replace;
        this.delete = delete;
        this.iconTP = tp;
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

        setIcon(iconTP);

        setFont(getFont().deriveFont(Font.PLAIN));

        if ( value instanceof DefaultMutableTreeNode) {
            if ( ((DefaultMutableTreeNode)value).getUserObject() instanceof TransplantInfo) {
                TransplantInfo t = (TransplantInfo) ((DefaultMutableTreeNode)value).getUserObject();
                if (t.getType().contains("replace")) {
                    setIcon(replace);
                } else if (t.getType().contains("add")) {
                    setIcon(add);
                } else if (t.getType().contains("delete")) {
                    setIcon(delete);
                }

                if (t.isApplied()) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            } else if  ( ((DefaultMutableTreeNode)value).getUserObject() instanceof TransformationInfo) {
                TransformationInfo t = (TransformationInfo) ((DefaultMutableTreeNode)value).getUserObject();
                if ( t.getAppliedTransplant() != null ) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
        }

        return this;
    }
}
