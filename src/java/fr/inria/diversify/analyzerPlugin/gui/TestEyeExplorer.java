package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.ComplainAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadTransformationsAction;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeExplorer extends SimpleToolWindowPanel {

    /**
     * Prefix of the all plugin's actions and component
     */
    public static final String PLUG_NAME_PREFIX = "TestEye.";

    /**
     * A proxy to access to all IntelliJ IDEA singletons
     */
    private IDEObjects ideObjects;

    /**
     * Parent of all components
     */
    public JPanel pnlContent;

    /**
     * Tree with the visible transformations
     */
    private Tree treeTransformations;

    /**
     * Label to show totals
     */
    private JLabel lblTotals;

    /**
     * A proxy to access IDE singleton objects
     */
    public class IDEObjects {
        public ActionManager getActionManager() {
            return ActionManager.getInstance();
        }
    }

    public TestEyeExplorer(Project project) {
        super(true, true);
        setIDEObjects(new IDEObjects());
        setContent(pnlContent);
        setToolbar(createToolbarPanel());
        registerActions();
    }

    private JPanel createToolbarPanel() {

        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new LoadTransformationsAction());
        final ActionToolbar actionToolBar = getIDEObjects().getActionManager().createActionToolbar(
                ActionPlaces.ANT_EXPLORER_TOOLBAR, group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);

        return buttonsPanel;
    }

    /**
     * Register all actions that are going to be used by the user
     */
    private void registerActions() {
        ActionManager m = getIDEObjects().getActionManager();
        m.registerAction(PLUG_NAME_PREFIX + ShowTransformationsInTree.class.getSimpleName(),
                new ShowTransformationsInTree(treeTransformations, lblTotals));
    }

    /**
     * Proxy to access IDE objects
     *
     * @return
     */
    public IDEObjects getIDEObjects() {
        return ideObjects;
    }

    public void setIDEObjects(IDEObjects objects) {
        ideObjects = objects;
    }
}
