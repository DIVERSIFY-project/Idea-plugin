package fr.inria.diversify.analyzerPlugin.ut.component;

import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import fr.inria.diversify.analyzerPlugin.model.orders.AlphabeticallOrder;
import fr.inria.diversify.analyzerPlugin.model.orders.TotalTransplantsOrder;
import fr.inria.diversify.diversification.InputProgram;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static junit.framework.Assert.*;

/**
 * Test the project component
 *
 * Created by marodrig on 02/02/2015.
 */
public class TestEyeProjectComponentTest {

    public static class MyClassiferFactory extends ClassifierFactory {

        @Override
        public List<TransformClasifier> buildClassifiers() {
            ArrayList<TransformClasifier> clasifiers = new ArrayList<TransformClasifier>();
            clasifiers.add(new ReplaceClassifier());
            clasifiers.add(new NonReplaceClassifier());
            return clasifiers;
        }

    }

    private static List<TransformationInfo> getInfos(InputProgram program) {
        //Create a list of transformations
        List<TransformationInfo> infos  = new ArrayList<>(
                TransformationInfo.fromTransformations(
                        createTransformations(program)));
        return infos;
    }

    private TestEyeProjectComponent initComponent() {
        return initComponent(new FakeProject());
    }

    public static TestEyeProjectComponent initComponent(Project p) {

        //Returns a component loaded with a MockInputProgram
        TestEyeProjectComponent component = p.getComponent(TestEyeProjectComponent.class);
        //Create a list of transformations
        component.setInfos(getInfos(component.getProgram()));
        //Set a simple classifier factory
        component.setClassifierFactory(new MyClassiferFactory());
        return component;
    }

    /**
     * Test the filtering functionality
     */
    @Test
    public void testFilteringUnclassifiedFalse() {
        TestEyeProjectComponent component = initComponent();
        //Set visibility of unclassified to false
        component.setVisibleClassifiers(TestEyeProjectComponent.UNCLASSIFIED, false);

        component.setVisibleClassifiers(ReplaceClassifier.class, false);
        component.filterAndSort(new ProgressIndicatorBase());

        //Test that all but one is visible. The transplant are created from the "createTransformations" method
        assertEquals(1, component.getVisibleInfos().size());
        assertEquals(2, component.getVisibleInfos().get(0).getVisibleTransplants());

        //Invert the situation
        component.switchClassifier(ReplaceClassifier.class);
        component.switchClassifier(NonReplaceClassifier.class);
        //Filter
        component.filterAndSort(new ProgressIndicatorBase());
        //Verify
        assertEquals(2, component.getVisibleInfos().size());
        assertEquals(1, component.getVisibleInfos().get(0).getVisibleTransplants());
    }

    /**
     * Test the filtering functionality when unclassified is true
     */
    @Test
    public void testFilteringUnclassifiedTrue() {
        TestEyeProjectComponent component = initComponent();

        //The unclassified
        TransformationInfo t = new TransformationInfo();
        t.setPosition("Unclassified position");
        t.getTransplants().add(new TransplantInfo());
        component.getInfos().add(t);

        //Set visibility of unclassified to false
        component.setVisibleClassifiers(TestEyeProjectComponent.UNCLASSIFIED, true);

        //Check that the unclassified
        component.switchClassifier(ReplaceClassifier.class);
        component.switchClassifier(NonReplaceClassifier.class);
        //Filter
        component.filterAndSort(new ProgressIndicatorBase());
        //Verify
        assertEquals(1, component.getVisibleInfos().size());
        assertEquals("Unclassified position", component.getVisibleInfos().get(0).getPosition());
    }


    /**
     * Test the filtering functionality when unclassified is true
     */
    @Test
    public void testSorting() {
        TestEyeProjectComponent component = initComponent();
        component.setOrder(new TotalTransplantsOrder());
        component.filterAndSort(new ProgressIndicatorBase());
        assertEquals(3, component.getVisibleInfos().get(0).getTransplants().size());
        assertEquals(1, component.getVisibleInfos().get(1).getTransplants().size());

        //Now use another sorter
        component.setOrder(new AlphabeticallOrder());
        component.filterAndSort(new ProgressIndicatorBase());
        assertEquals(1, component.getVisibleInfos().get(0).getTransplants().size());
        assertEquals(3, component.getVisibleInfos().get(1).getTransplants().size());
    }

    /**
     * Test the filtering functionality when unclassified is true
     */
    @Test
    public void testHideAll() {
        TestEyeProjectComponent component = initComponent();
        component.hideAll(new ProgressIndicatorBase());
        assertEquals(0, component.getVisibleInfos().size());
        assertTrue(component.getShowClassifiersIntersection());
    }

    /**
     * Test the filtering functionality when unclassified is true
     */
    @Test
    public void testshowAll() {
        TestEyeProjectComponent component = initComponent();
        component.switchClassifier(ReplaceClassifier.class);
        component.showAll(new ProgressIndicatorBase());
        assertEquals(2, component.getVisibleInfos().size());
        assertFalse(component.getShowClassifiersIntersection());
    }
}
