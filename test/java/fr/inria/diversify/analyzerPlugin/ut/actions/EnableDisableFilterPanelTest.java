package fr.inria.diversify.analyzerPlugin.ut.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.FakeAnActionEvent;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.actions.display.EnableDisableFilterPanel;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.ut.MockInputProgram;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.util.ArrayList;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 02/02/2015.
 */
public class EnableDisableFilterPanelTest {

    /**
     * Produces a fake action event that will feed the action with proper data
     * @return
     */
    private AnActionEvent getActionEvent(List<TransformationInfo> infos) {
        //Make a fake project
        FakeProject f = new FakeProject();
        f.getComponent(TestEyeProjectComponent.class).setInfos(infos);
        return new FakeAnActionEvent(f);
    }

    /**
     * Test the enable or disabling of the component
     */
    @Test
    public void testEnableDisable() {
        FilterPanel f = new FilterPanel();
        f.setEnabled(false);
        EnableDisableFilterPanel action = new EnableDisableFilterPanel(f);

        //Create the transformations
        List<TransformationInfo> infos  = new ArrayList<>();
        action.actionPerformed(getActionEvent(infos));
        assertFalse(f.isEnabled());

        infos.add(new TransformationInfo());
        action.actionPerformed(getActionEvent(infos));
        assertTrue(f.isEnabled());
    }

}
