package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import org.jetbrains.annotations.NotNull;

/**
 * Action in charge of filtering and sorting all transformation info
 *
 * Created by marodrig on 29/01/2015.
 */
public class FilterAndSortAction extends TestEyeAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {

        final TestEyeProjectComponent component = event.getProject().getComponent(TestEyeProjectComponent.class);

        //Get the Tranformation's tree thanks tho the data context magic in IntelliJ IDEA framework
        final TreeTransformations tree = event.getData(TreeTransformations.TEST_EYE_TREE_TRANSFORMATIONS);

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(),
                "Sorting and filtering...") {
            public void onSuccess() {
                super.onSuccess();
                tryExecute(ShowTransformationsInTree.class, event);
            }
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    component.filterAndSort(progressIndicator);
                } catch (Exception e) {
                    hardComplain("Cannot filter " , e);
                }
            }
        });
    }
}
