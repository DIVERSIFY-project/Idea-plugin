package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

/**
 * Created by marodrig on 29/01/2015.
 */
public class SwitchClasifierAction extends TestEyeAction {

    private final Class<? extends TransformClassifier> classifierClass;

    public SwitchClasifierAction(Class<? extends TransformClassifier> klass, String description) {
        super(description, description, IconUtil.getAddIcon());
        this.classifierClass = klass;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        TestEyeProjectComponent c = e.getProject().getComponent(TestEyeProjectComponent.class);
        c.switchClassifier(classifierClass);
        tryExecute(FilterAndSortAction.class, e);
    }
}
