package fr.inria.diversify.analyzerPlugin.components;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ApplicationComponent;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.*;
import fr.inria.diversify.analyzerPlugin.actions.searching.FilterAndSortAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Register all actions and serves as a middle man between interacting actions
 *
 * Created by marodrig on 02/02/2015.
 */
public class TestEyeApplicationComponentImpl implements ApplicationComponent, TestEyeApplicationComponent {

    /**
     * Prefix of the all plugin's actions and component
     */
    public static final String PLUG_NAME_PREFIX = "TestEye.";

    private static final String CANT_FIND_ACTION = "Can't find action ";

    private TestEyeExplorer explorer;

    private IDEObjects ideObjects;

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TestEye.application";
    }


    @Override
    public TestEyeExplorer getExplorer() {
        return explorer;
    }

    public void setExplorer(TestEyeExplorer explorer) {
        this.explorer = explorer;
    }

    private void registerAction(String id, TestEyeAction action) {
        ActionManager m = ideObjects.getActionManager();
        m.registerAction(id, action);
        action.setIdeObjects(ideObjects);
    }

    @Override
    public void registerActions(IDEObjects ideObjects) {

        setIdeObjects(ideObjects);

        registerAction(PLUG_NAME_PREFIX + ShowTransformationsInTree.class.getSimpleName(),
                new ShowTransformationsInTree(explorer.getTreeTransformations(), explorer.getLblTotals()));

        registerAction(PLUG_NAME_PREFIX + ShowTransformationProperties.class.getSimpleName(),
                new ShowTransformationProperties(explorer.getTblProperties()));

        registerAction(PLUG_NAME_PREFIX + ShowCoverageInfo.class.getSimpleName(),
                new ShowCoverageInfo(explorer.getTreeTransformations(), explorer.getTreeCoverage()));

        registerAction(PLUG_NAME_PREFIX + FilterAndSortAction.class.getSimpleName(), new FilterAndSortAction());

        registerAction(PLUG_NAME_PREFIX + EnableDisableFilterPanel.class.getSimpleName(),
                new EnableDisableFilterPanel(explorer.getLstFilters()));

        registerAction(PLUG_NAME_PREFIX + SeekCodeTransformation.class.getSimpleName(),
                new SeekCodeTransformation(explorer.getTreeTransformations()));

        registerAction(PLUG_NAME_PREFIX + ShowErrorsAction.class.getSimpleName(),
                new ShowErrorsAction(explorer.getLstErrors()));

    }

    /**
     * Tries to execute an action
     *
     * @param actionClass class of the action to execute
     * @param event       Event to pass on to the action
     */
    @Override
    public void tryExecute(Class<?> actionClass, AnActionEvent event)  {
        AnAction a = getAction(event, actionClass);
        a.actionPerformed(event);

    }

    /**
     * Returns an action from the action manager given its class
     *
     * @param event Event containing the action manager
     * @param c     Class of the action
     * @return
     */
    private AnAction getAction(AnActionEvent event, Class<?> c) {
        String actionName = "TestEye." + c.getSimpleName();

        if (event.getActionManager().getAction(actionName) == null)
            throw new IllegalStateException(CANT_FIND_ACTION + actionName);

        return event.getActionManager().getAction(actionName);
    }

    public void setIdeObjects(IDEObjects ideObjects) {
        this.ideObjects = ideObjects;
    }
}
