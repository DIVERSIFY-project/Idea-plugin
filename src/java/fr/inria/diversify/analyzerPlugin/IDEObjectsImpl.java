package fr.inria.diversify.analyzerPlugin;


import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponent;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponentImpl;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;

/**
 * A proxy class to access the IDE objects
 *
 * Created by marodrig on 02/02/2015.
 */
public class IDEObjectsImpl implements IDEObjects {

    /**
     * Returns the action manager
     * @return The ActionManager
     */
    @Override
    public ActionManager getActionManager() {
        return ActionManager.getInstance();
    }

    /**
     * Returns the Application component
     * @return The TestEyeApplicationComponent
     */
    @Override
    public TestEyeApplicationComponent getApplicationComponent() {
        return ApplicationManager.getApplication().getComponent(TestEyeApplicationComponentImpl.class);
    }


}
