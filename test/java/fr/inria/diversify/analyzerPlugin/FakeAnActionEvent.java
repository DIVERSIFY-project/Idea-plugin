package fr.inria.diversify.analyzerPlugin;

import com.intellij.ide.impl.DataManagerImpl;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Created by marodrig on 28/01/2015.
 */
public class FakeAnActionEvent extends AnActionEvent {

    private ActionManager actionManager;

    private Project project;

    public FakeAnActionEvent(Project p) {
        this();
        project = p;
        actionManager = new FakeActionManager();
    }

    public FakeAnActionEvent(ActionManager m) {
        this();
        actionManager = m;
        project = new FakeProject();
    }

    public FakeAnActionEvent() {
        super(new KeyEvent(new JBLabel(), 0, 0, 0, 0, '.'),
                new DataManagerImpl.MyDataContext(null), "_",
                new Presentation(),
                new FakeActionManager(), 0);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public ActionManager getActionManager() {
        return actionManager;
    }

}
