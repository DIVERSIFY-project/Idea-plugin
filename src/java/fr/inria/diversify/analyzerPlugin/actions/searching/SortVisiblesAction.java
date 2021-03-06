package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.orders.Order;
import icons.TestEyeIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Action in charge of filtering and sorting all transformation info
 *
 * Created by marodrig on 29/01/2015.
 */
public class SortVisiblesAction extends TestEyeAction {

    private final boolean filter;
    private Comparator<TransformationInfo> comparator;

    /**
     * Indicates the comparator and if the set of transformations must be filtered before the sorting
     * @param comparator Comparator to compare to
     * @param filter Indicate if we must filter
     */
    public SortVisiblesAction(Order comparator, boolean filter) {
        super(comparator.getDescription(), comparator.getDescription(), TestEyeIcons.Sort);
        this.comparator = comparator;
        this.filter = filter;
    }

    public SortVisiblesAction(Order comparator) {
        this(comparator, false);
    }

    @Override
    public void actionPerformed(final AnActionEvent event) {

        final TestEyeProjectComponent component = event.getProject().getComponent(TestEyeProjectComponent.class);

        //Get the Tranformation's tree thanks tho the data context magic in IntelliJ IDEA framework
        final TreeTransformations tree = event.getData(TreeTransformations.TEST_EYE_TREE_TRANSFORMATIONS);
        
        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(),
                "Sorting and filtering (This will be done only once)...") {
            public void onSuccess() {
                super.onSuccess();
                tryExecute(ShowTransformationsInTree.class, event);
            }
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    component.setOrder(comparator);
                    if ( filter ) component.filterAndSort(progressIndicator);
                    else component.sort();
                } catch (Exception e) {
                    hardComplain("Cannot filter " , e);
                }
            }
        });
    }
}
