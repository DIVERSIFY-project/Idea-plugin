package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.actionSystem.ActionManager;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponent;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponentImpl;

/**
 *  A proxy interface to access the IDE objects
 *
 * Created by marodrig on 02/02/2015.
 *
 */
public interface IDEObjects {

    ActionManager getActionManager();

    TestEyeApplicationComponent getApplicationComponent();

}