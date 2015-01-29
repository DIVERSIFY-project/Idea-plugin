package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.MultiColumnList;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.ComplainAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationProperties;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadTransformationsAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private TreeTransformations treeTransformations;

    /**
     * Label to show totals
     */
    private JLabel lblTotals;

    /**
     * Table for transformations properties
     */
    private TransformationsProperties tblProperties;

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

        Object[] s = new Object[]{"Property", "Value"};
        DefaultTableModel dtm = new DefaultTableModel(s, 0);
        tblProperties.setModel(dtm);
    }

    /**
     * Creates the tool bar panel in the top
     * @return
     */
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
        //TODO: Refactor this to a "registrar"
        ActionManager m = getIDEObjects().getActionManager();

        m.registerAction(PLUG_NAME_PREFIX + ShowTransformationsInTree.class.getSimpleName(),
                new ShowTransformationsInTree(treeTransformations, lblTotals));

        m.registerAction(PLUG_NAME_PREFIX + ShowTransformationProperties.class.getSimpleName(),
                new ShowTransformationProperties(tblProperties));
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
        treeTransformations.setIDEObjects(ideObjects);
    }
}
