package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;

/**
 * Created by marodrig on 29/01/2015.
 */
public class HideAllClasifierAction extends TestEyeAction {

    public HideAllClasifierAction() {
        super("Hide all", "Hide all", IconUtil.getAddIcon());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestEyeProjectComponent c = e.getProject().getComponent(TestEyeProjectComponent.class);
        c.setAllClassificationsVisibility(false);
        tryExecute(FilterAndSortAction.class, e);
    }
}
