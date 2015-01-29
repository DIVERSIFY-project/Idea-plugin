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


    private static final Class<? extends TransformClasifier> UNCLASSIFIED = null;

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
                getAction(event, ShowTransformationsInTree.class).actionPerformed(event);
            }

            public void run(@NotNull ProgressIndicator progressIndicator) {
                int i = 0;

                try {
                    String pomPath = component.getProgram().getProgramDir() + File.separator + "pom.xml";
                    String srcDir = component.getProgram().getSourceCodeDir();

                    if (component.getVisibleInfos() == null)
                        component.setVisibleInfos(new ArrayList<TransformationInfo>());
                    else component.getVisibleInfos().clear();

                    int progress = 1;
                    for (TransformationInfo p : reps) {

                        if ( progressIndicator.isCanceled() ) return;
                        progressIndicator.setFraction((double) progress / (double) reps.size());
                        progress++;

                        for (TransplantInfo transplant : p.getTransplants()) {
                            /*
                            //This is already done in the loading process:
                            try {
                                i++;
                                transplant.getTransformation()(transplant, pomPath, srcDir);
                            } catch (RuntimeException rex) {
                                //Skip this transplant
                                Log.warn(i + ". There was a problem with " + transplant.toString() + ". Because " + rex.getMessage());
                                softComplain(rex.getMessage(), rex);
                                p.getTransplants().remove(transplant);
                                break;
                            }*/

                            transplant.setVisibility(TransplantInfo.Visibility.unclassified);
                            for (TransformClasifier c : component.getClassifiers()) {
                                float v;
                                //the only way classification functions modify the score assigned
                                //is by user input, therefore only user filters must be reclassified each time
                                if (!c.isUserFilter() && transplant.isAlreadyClassified(c.getDescription())) {
                                    //retrieve classification already assignment to the transformation
                                    v = transplant.getClassification(c.getDescription());
                                } else {
                                    // evaluates the transformation
                                    v = c.value(transplant);
                                    transplant.setClassification(c.getDescription(), v);
                                }

                                //sets the visibility on/off depending on the show intersection option
                                if (v != 0) {
                                    if (component.isFilterVisible(c.getClass())) {
                                        transplant.setVisibility(TransplantInfo.Visibility.show);
                                        if (component.showClassifiersIntersection()) break;
                                    } else {
                                        transplant.setVisibility(TransplantInfo.Visibility.hide);
                                        if (!component.showClassifiersIntersection()) break;
                                    }
                                }
                            }
                            //If no classification functions and was able to classify the transplant
                            //then the transplant become  unclassified and its visibility is assignment depending
                            //on a special case of classification function
                            if (transplant.getVisibility() == TransplantInfo.Visibility.unclassified) {
                                TransplantInfo.Visibility vis = component.isFilterVisible(UNCLASSIFIED) ?
                                        TransplantInfo.Visibility.show : TransplantInfo.Visibility.hide;
                                transplant.setVisibility(vis);
                            }
                        }
                    }

                } catch (Exception e) {
                    hardComplain("Cannot perform weighting", e);
                }
            }
        });
    }
}
