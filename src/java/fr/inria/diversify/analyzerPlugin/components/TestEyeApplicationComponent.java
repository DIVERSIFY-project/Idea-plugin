package fr.inria.diversify.analyzerPlugin.components;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.IDEObjects;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;

/**
 * Created by marodrig on 02/02/2015.
 */
public interface TestEyeApplicationComponent {

    TestEyeExplorer getExplorer();

    void registerActions(IDEObjects ideObjects);

    void tryExecute(Class<?> actionClass, AnActionEvent event);
}
