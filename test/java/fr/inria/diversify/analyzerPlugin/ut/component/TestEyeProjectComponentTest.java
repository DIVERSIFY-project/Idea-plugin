package fr.inria.diversify.analyzerPlugin.ut.component;

import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static junit.framework.Assert.assertEquals;

/**
 * Test the project component
 *
 * Created by marodrig on 02/02/2015.
 */
public class TestEyeProjectComponentTest {

    public class MyClassiferFactory extends ClassifierFactory {

        @Override
        public List<TransformClasifier> buildClassifiers() {
            ArrayList<TransformClasifier> clasifiers = new ArrayList<TransformClasifier>();
            clasifiers.add(new ReplaceClassifier());
            clasifiers.add(new NonReplaceClassifier());
            return clasifiers;
        }

    }

    /**
     * Test the filtering functionality
     */
    @Test
    public void testFilteringUnclassifiedFalse() {
        Project p = new FakeProject();
        //Returns a component loaded with a MockInputProgram
        TestEyeProjectComponent component = p.getComponent(TestEyeProjectComponent.class);

        //Create a list of transformations
        List<TransformationInfo> infos  = new ArrayList<>(
                TransformationInfo.fromTransformations(createTransformations(component.getProgram())));
        component.setInfos(infos);

        //Set a simple classifier factory
        component.setClassifierFactory(new MyClassiferFactory());

        //Set visibility of unclassified to false
        component.setVisibleClassifiers(TestEyeProjectComponent.UNCLASSIFIED, false);

        component.setVisibleClassifiers(ReplaceClassifier.class, false);
        component.filterAndSort(new ProgressIndicatorBase());

        //Test that all but one is visible. The transplant are created from the "createTransformations" method
        assertEquals(1, component.getVisibleInfos().size());
        assertEquals(2, component.getVisibleInfos().get(0).getVisibleTransplants());

        //Invert the situation
        component.setVisibleClassifiers(ReplaceClassifier.class, true);
        component.setVisibleClassifiers(NonReplaceClassifier.class, false);
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
        Project p = new FakeProject();
        //Returns a component loaded with a MockInputProgram
        TestEyeProjectComponent component = p.getComponent(TestEyeProjectComponent.class);

        //Create a list of transformations
        List<TransformationInfo> infos  = new ArrayList<>(
                TransformationInfo.fromTransformations(
                        createTransformations(component.getProgram())));

        //The unclassified
        TransformationInfo t = new TransformationInfo();
        t.setPosition("Unclassified position");
        t.getTransplants().add(new TransplantInfo());
        infos.add(t);

        component.setInfos(infos);

        //Set a simple classifier factory
        component.setClassifierFactory(new MyClassiferFactory());

        //Set visibility of unclassified to false
        component.setVisibleClassifiers(TestEyeProjectComponent.UNCLASSIFIED, true);

        //Check that the unclassified
        component.setVisibleClassifiers(ReplaceClassifier.class, false);
        component.setVisibleClassifiers(NonReplaceClassifier.class, false);
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

    }
}
