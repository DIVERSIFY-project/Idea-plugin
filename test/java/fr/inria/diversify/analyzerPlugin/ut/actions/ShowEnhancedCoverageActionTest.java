package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.ui.treeStructure.Tree;
import fr.inria.diversify.analyzerPlugin.*;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowCoverageInfo;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.getInfos;
import static fr.inria.diversify.analyzerPlugin.ut.metadata.EnhancedCoverageProcessorTest.getLogs;

/**
 * Created by marodrig on 04/02/2015.
 */
public class ShowEnhancedCoverageActionTest {

    /**
     * Test the perform action of the ShowEnhancedCoverageAction
     * @throws LoadingException
     */
    @Test
    public void testPerformAction() throws LoadingException {
        ArrayList<TransformationInfo> infos = getInfos();
        EnhancedCoverageProcessor p = new EnhancedCoverageProcessor(infos);
        p.process(getLogs());

        TreeTransformations tree = new TreeTransformations();
        tree.setInfos(infos);

        ShowCoverageInfo action = new ShowCoverageInfo(tree);
        action.setIdeObjects(new FakeIDEObjects());
        action.actionPerformed(new FakeAnActionEvent(new FakeProject()));

    }

}
