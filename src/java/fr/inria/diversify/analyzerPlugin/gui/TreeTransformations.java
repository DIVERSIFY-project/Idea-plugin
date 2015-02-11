package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.PopupHandler;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowCoverageInfo;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.replay.ApplyTransformation;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

/**
 * Created by marodrig on 29/01/2015.
 */
public class TreeTransformations extends CodePositionTree implements com.intellij.openapi.actionSystem.DataProvider {

    public static final DataKey<TreeTransformations>
            TEST_EYE_TREE_TRANSFORMATIONS = DataKey.create("test.eye.tree.transformations");

    private Collection<TransformationInfo> infos;
    private int transplantCount;
    private int potCount;

    public TreeTransformations() {
        super(getDefaultTreeModel());
        init();
    }

    public TreeTransformations(TreeNode root) {
        super(new DefaultTreeModel(root, false));
        init();
    }

    public TreeTransformations(TreeModel treemodel) {
        super(treemodel);
        init();
    }

    @Override
    protected void init() {
        super.init();
        installDoubleClick();
        installPopup();
        setCellRenderer(new TransformationsNodeRenderer());
    }

    /**
     * Installs the double click
     */
    @Override
    protected void installDoubleClick() {
        super.installDoubleClick();
        final MouseListener listener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ActionManager m = ActionManager.getInstance();
                m.tryToExecute(m.getAction(ShowTransformationProperties.ID), e, e.getComponent(), null, true);
                m.tryToExecute(m.getAction(ShowCoverageInfo.ID), e, e.getComponent(), null, true);
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
                CodePositionTree me = (CodePositionTree)comp;
                final DefaultActionGroup popupGroup = new DefaultActionGroup();
                popupGroup.add(new SeekCodeTransformation(me));
                if ( me.getSelectedCodePosition() instanceof TransplantInfo ) {
                    popupGroup.add(new ApplyTransformation((CodePositionTree) comp));
                }
                ActionPopupMenu popupMenu = getIDEObjects().getActionManager().createActionPopupMenu(
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


    /**
     * Set the infos to show in the interface
     * @param infos Infos to show in the interface
     */
    public void setInfos(Collection<TransformationInfo> infos) {
        this.infos = infos;
        transplantCount = 0;
        potCount = 0;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Transformations");
        DefaultTreeModel model = new DefaultTreeModel(root);

        for (TransformationInfo tp : infos) {
            DefaultMutableTreeNode rep = new DefaultMutableTreeNode(tp);
            for (TransplantInfo t : tp.getTransplants()) {
                if (t.getVisibility() == TransplantInfo.Visibility.show) {
                    transplantCount++;
                    rep.insert(new DefaultMutableTreeNode(t), rep.getChildCount());
                }
            }
            if (rep.getChildCount() > 0) {
                potCount++;
                root.insert(rep, root.getChildCount());
            }
        }
        setModel(model);
    }

    /**
     * Get the infos being shown in the interface
     */
    public Collection<TransformationInfo> getInfos() {
        return infos;
    }

    public int getTransplantCount() {
        return transplantCount;
    }

    public int getPotCount() {
        return potCount;
    }
}
