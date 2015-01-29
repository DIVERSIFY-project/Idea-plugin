package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;

/**
 *
 * Created by marodrig on 29/01/2015.
 */
public class ShowAllClasifierAction extends TestEyeAction {

    public ShowAllClasifierAction() {
        super("Show all", "Show all", IconUtil.getAddIcon());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestEyeProjectComponent c = e.getProject().getComponent(TestEyeProjectComponent.class);
        c.setAllClassificationsVisibility(true);
        getAction(e, FilterAndSortAction.class).actionPerformed(e);
    }
}
