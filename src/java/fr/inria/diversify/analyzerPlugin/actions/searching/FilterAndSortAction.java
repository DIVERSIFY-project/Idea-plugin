package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import org.jetbrains.annotations.NotNull;
import org.kevoree.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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

        final Collection<TransformationInfo> reps = component.getInfos();

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(),
                "Sorting and filtering (This will be done only once)...") {
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
