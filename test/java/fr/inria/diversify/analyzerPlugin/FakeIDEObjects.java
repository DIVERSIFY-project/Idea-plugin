package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.components.TestEyeApplicationComponent;
import fr.inria.diversify.analyzerPlugin.gui.TestEyeExplorer;

import java.util.HashMap;

/**
 * Created by marodrig on 02/02/2015.
 */
public class FakeIDEObjects implements IDEObjects {

    public HashMap<String, Integer> tryExecuteCount = new HashMap<>();

    @Override
    public ActionManager getActionManager() {
        return new FakeActionManager();
    }

    @Override
    public TestEyeApplicationComponent getApplicationComponent() {
        return new TestEyeApplicationComponent() {
            @Override
            public TestEyeExplorer getExplorer() {
                return null;
            }

            @Override
            public void registerActions(IDEObjects ideObjects) {

            }

            @Override
            public void tryExecute(Class<?> actionClass, AnActionEvent event) {
                Integer i = tryExecuteCount.get(actionClass.getName());
                if ( i == null ) i = 0;
                tryExecuteCount.put(actionClass.getName(), i + 1);
            }
        };
    }
}
