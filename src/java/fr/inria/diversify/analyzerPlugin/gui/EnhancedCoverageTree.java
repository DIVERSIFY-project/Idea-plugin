package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by marodrig on 03/02/2015.
 */
public class EnhancedCoverageTree extends Tree implements com.intellij.openapi.actionSystem.DataProvider {

    public static final DataKey<EnhancedCoverageTree>
            ENHANCED_COVERAGE_TREE_DATA_KEY = DataKey.create("test.eye.tree.coverage");


    public EnhancedCoverageTree() {
        super((TreeModel) getDefaultTreeModel());
        init();
    }

    public EnhancedCoverageTree(TreeNode root) {
        super((TreeModel) (new DefaultTreeModel(root, false)));
        init();
    }

    public EnhancedCoverageTree(TreeModel treemodel) {
        super(treemodel);
        init();
    }

    private void init() {
        setModel(null);
        setToggleClickCount(0);
        setRootVisible(false);
        setCellRenderer(new TransformationsNodeRenderer());
        //installPopup();
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

    @Nullable
    @Override
    public Object getData(String s) {
        return s.equals(ENHANCED_COVERAGE_TREE_DATA_KEY.getName()) ? this : null;
    }
}
