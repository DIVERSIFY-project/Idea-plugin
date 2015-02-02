package fr.inria.diversify.analyzerPlugin.gui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.IDEObjectsImpl;
import fr.inria.diversify.analyzerPlugin.actions.loading.LoadTransformationsAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponentImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeExplorer extends SimpleToolWindowPanel {

    private static final Logger logger = Logger.getInstance("#" + TestEyeExplorer.class.getName());


    /**
     * A proxy to access to all IntelliJ IDEA singletons
     */
    private IDEObjects ideObjects;

    /**
     * Parent of all components
     */
    public JPanel pnlContent;

    public JPanel getPnlContent () {
        return pnlContent;
    }

    /**
     * Tree with the visible transformations
     */
    private TreeTransformations treeTransformations;

    public TreeTransformations getTreeTransformations() {
        return treeTransformations;
    }

    /**
     * Label to show totals
     */
    private JLabel lblTotals;

    public JLabel getLblTotals() {
        return lblTotals;
    }

    /**
     * Table for transformations properties
     */
    private TransformationsProperties tblProperties;

    public TransformationsProperties getTblProperties() {return tblProperties;}

    /**
     * List with all the filters
     */
    private FilterPanel lstFilters;

    public FilterPanel getLstFilters() { return lstFilters; }

    public TestEyeExplorer(Project project) {
        super(true, true);

        logger.info("Hi, I'm logging!");

        setIDEObjects(new IDEObjectsImpl());
        setContent(pnlContent);
        setToolbar(createToolbarPanel());
        registerActions();

        Object[] s = new Object[]{"Property", "Value"};
        DefaultTableModel dtm = new DefaultTableModel(s, 0);
        tblProperties.setModel(dtm);
        lstFilters.setEnabled(false);
    }

    /**
     * Creates the tool bar panel in the top
     *
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
        TestEyeApplicationComponentImpl comp =
                (TestEyeApplicationComponentImpl)getIDEObjects().getApplicationComponent();
        comp.setExplorer(this);
        comp.registerActions(ideObjects);
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
        tblProperties.setIDEObjects(ideObjects);
        lstFilters.setIdeObject(ideObjects);
    }
}
