package fr.inria.diversify.analyzerPlugin.ut.component;

import com.intellij.openapi.fileChooser.FileChooser;
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
import fr.inria.diversify.persistence.PersistenceException;
import fr.inria.diversify.persistence.json.input.JsonHeaderInput;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.input.JsonHeaderInputTest;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformationsJSON;
import static fr.inria.diversify.ut.json.SectionTestUtils.createTransformationsJSONObjectWithErrors;
import static junit.framework.Assert.*;

/**
 * Test the project component
 * <p/>
 * Created by marodrig on 02/02/2015.
 */
@RunWith(JMockit.class)
public class TestEyeProjectComponentTest {

    /**
     * A classifier factory building two classifiers to test classifying functionality
     */
    public static class MyClassiferFactory extends ClassifierFactory {
        @Override
        public List<TransformClasifier> buildClassifiers() {
            ArrayList<TransformClasifier> clasifiers = new ArrayList<TransformClasifier>();
            clasifiers.add(new ReplaceClassifier());
            clasifiers.add(new NonReplaceClassifier());
            return clasifiers;
        }
    }

    /**
     * Get a set of infos to test with
     *
     * @param program
     * @return
     */
    private static List<TransformationInfo> getInfos(InputProgram program) {
        //Create a list of transformations
        List<TransformationInfo> infos = new ArrayList<>(
                TransformationInfo.fromTransformations(
                        createTransformations(program), new ArrayList<String>()));
        return infos;
    }

    private TestEyeProjectComponent initComponent() {
        return initComponent(new FakeProject());
    }

    /**
     * Inits a component to test, setting a group of infos and a classification factory
     *
     * @param p
     * @return
     */
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
     * Test the hide all functionality
     */
    @Test
    public void testHideAll() {
        TestEyeProjectComponent component = initComponent();
        component.hideAll(new ProgressIndicatorBase());
        assertEquals(0, component.getVisibleInfos().size());
        assertTrue(component.getShowClassifiersIntersection());
    }

    /**
     * Test the show all functionality
     */
    @Test
    public void testShowAll() {
        TestEyeProjectComponent component = initComponent();
        component.switchClassifier(ReplaceClassifier.class);
        component.showAll(new ProgressIndicatorBase());
        assertEquals(2, component.getVisibleInfos().size());
        assertFalse(component.getShowClassifiersIntersection());
    }

    /**
     * Test the load infos functionality. OK case
     */
    @Test
    public void testLoadInfos(@Mocked final JsonHeaderInput anyHeader) {
        InputProgram p = new MockInputProgram();
        TestEyeProjectComponent component = new TestEyeProjectComponent(new FakeProject());
        component.setProgram(p);

        //Read the transformations
        JSONObject obj = createTransformationsJSON(p);
        InputStreamReader r = new InputStreamReader(
                new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
        component.loadInfos(r);
        assertEquals(2, component.getInfos().size());
    }

    /**
     * Test the load infos functionality with errors
     */
    @Test
    public void testLoadInfos_WithErrors(@Mocked final JsonHeaderInput anyHeader) throws JSONException {
        InputProgram p = new MockInputProgram();
        JSONObject obj = createTransformationsJSONObjectWithErrors(p);
        TestEyeProjectComponent component = new TestEyeProjectComponent(new FakeProject());
        component.setProgram(p);
        //Read the transformations
        InputStreamReader r = new InputStreamReader(
                new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
        component.loadInfos(r);
        assertEquals(3, component.getLogMessages().size());
    }

    /**
     * Test the load infos functionality with errors
     */
    @Test
    public void testLoadInfos_WithHeaderErrors(@Mocked final JsonHeaderInput anyHeader) throws JSONException {
        //Mock the header so always thrown an exception
        new Expectations() {{
            anyHeader.read((HashMap<Integer, Transformation>) any);
            result = new PersistenceException("Boo");
        }};

        InputProgram p = new MockInputProgram();
        JSONObject obj = createTransformationsJSON(p);
        TestEyeProjectComponent component = new TestEyeProjectComponent(new FakeProject());
        component.setProgram(p);
        //Read the transformations
        InputStreamReader r = new InputStreamReader(
                new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
        try {
            component.loadInfos(r);
            fail("Exception expected");
        } catch (PersistenceException e) {
            //Yohoo catched!
        }
    }
}
