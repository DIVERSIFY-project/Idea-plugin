package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by marodrig on 29/01/2015.
 */
public class HideAllClasifierAction extends TestEyeAction {

    private final FilterPanel filterPanel;

    public HideAllClasifierAction(FilterPanel panel) {
        super("Hide all", "Hide all", IconUtil.getAddIcon());
        filterPanel = panel;
    }

    @Override
    public void actionPerformed(final AnActionEvent event) {

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(),
                "Sorting and filtering (This will be done only once)...") {

            @Override
            public void onSuccess() {
                super.onSuccess();
                tryExecute(ShowTransformationsInTree.class, event);
            }

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    TestEyeProjectComponent component = event.getProject().getComponent(TestEyeProjectComponent.class);
                    component.hideAll(progressIndicator);
                } catch (Exception e) {
                    hardComplain("Cannot hide all ", e);
                }
            }
        });

        if ( filterPanel != null ) {
            filterPanel.uncheckAllNoTriggerEvent();
            filterPanel.setShowIntersectionNoTriggerEvent(true);
        }
    }
}
