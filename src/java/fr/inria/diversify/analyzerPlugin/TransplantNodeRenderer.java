package fr.inria.diversify.analyzerPlugin;

import javax.enterprise.inject.Default;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
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
            if ( ((DefaultMutableTreeNode)value).getUserObject() instanceof Transplant ) {
                Transplant t = (Transplant) ((DefaultMutableTreeNode)value).getUserObject();
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
            } else if  ( ((DefaultMutableTreeNode)value).getUserObject() instanceof TransformationRepresentation ) {
                TransformationRepresentation t = (TransformationRepresentation) ((DefaultMutableTreeNode)value).getUserObject();
                if ( t.getAppliedTransplant() != null ) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
        }

        return this;
    }
}
