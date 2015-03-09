package fr.inria.diversify.analyzerPlugin.ut.model;

import fr.inria.diversify.analyzerPlugin.FakeProject;
import fr.inria.diversify.analyzerPlugin.TestHelpers;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.json.input.JsonHeaderInput;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.SectionTestUtils;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import junit.framework.Assert;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static fr.inria.diversify.ut.json.SectionTestUtils.*;
import static fr.inria.diversify.ut.json.SectionTestUtils.createTransformationsJSONObjectWithErrors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 04/09/2014.
 */
@RunWith(JMockit.class)
public class TransformationInfoTest {

    @Deprecated
    private JSONObject createSnippet(String source, String pos) throws JSONException {
        JSONObject snippet = new JSONObject();
        snippet.put("sourceCode", source);
        snippet.put("position", pos);
        snippet.put("type", "CtLocalVariableImpl");
        return snippet;
    }

    @Deprecated
    private JSONObject createTransformation(JSONObject pot, JSONObject transplant) throws JSONException {
        JSONObject transformation = new JSONObject();
        transformation.put("transplantationPoint", pot);
        if (transplant != null) {
            transformation.put("transplant", transplant);
        }
        transformation.put("tindex", 0);
        transformation.put("name", "replace");
        return transformation;
    }

    /**
     * Test importing the data directly from a JSON object
     */
    @Test
    @Deprecated
    public void testFromJSON() throws JSONException {
        JSONObject s1 = createSnippet("the source code", "the.position:33");
        JSONObject s2 = createSnippet("the other source", "the.other.position:56");
        JSONObject transformation = createTransformation(s1, s2);

        TransformationInfo representation = new TransformationInfo();
        representation.fromJSONObject(transformation, new JSONObject());

        assertEquals("the.position:33", representation.getPosition());
        assertEquals("replace", representation.getType());
        assertEquals("the.other.position:56", representation.getTransplants().get(0).getPosition());
        assertEquals("the other source", representation.getTransplants().get(0).getSource());
    }

    /**
     * Test importing the data directly from a JSON object representing a delete
     */
    @Test
    @Deprecated
    public void testFromJSONDelete() throws JSONException {
        JSONObject snippet = createSnippet("the source code", "the.position:33");
        JSONObject transformation = createTransformation(snippet, null);

        TransformationInfo representation = new TransformationInfo();
        representation.fromJSONObject(transformation, new JSONObject());
        assertEquals("the.position:33", representation.getPosition());
        assertEquals("replace", representation.getType());
    }

    /**
     * Turns transformations in a JSON into a plugin friendly data format

     @Test public void testFromJson() {
     //Write the transformations
     InputProgram p = new MockInputProgram();
     List<Transformation> t = TestHelpers.createTransformations(p);
     JsonSosieOutputForUT out = new JsonSosieOutputForUT(t, "/uzr/h0m3/my.jzon", "myProj/pom.xml", "myGen/pom.xml");
     JSONObject obj = out.writeToJsonNow(); //We need to mock the File writer so no writing to file is done

     InputStreamReader r = new InputStreamReader(
     new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
     ArrayList<TransformationInfo> infos = new ArrayList<>(TransformationInfo.fromJSON(r, p));
     assertInfosWhereCreatedProperly(infos);
     }*/

    /**
     * Turns transformations into a plugin friendly data format
     */
    @Test
    public void testFromTransformations() {
        List<Transformation> t = TestHelpers.createTransformations(new MockInputProgram());
        ArrayList<TransformationInfo> infos = new ArrayList<>(
                TransformationInfo.fromTransformations(t, new ArrayList<String>()));
        assertInfosWhereCreatedProperly(infos);
    }

    /**
     * Test that errors are linked with the transformations
     */
    @Test
    public void testLinkErrorWithTransformation() {
        //Create a set of transformations
        List<Transformation> t = TestHelpers.createTransformations(new MockInputProgram());

        //Create a list of errors
        List<String> errors = Arrays.asList(new String[]{
                "WARNING: Transf " + TEST_ID_1 + ". asdaasd a99 as 0a a9",
                "WARNING: Transf " + TEST_ID_2 + ". aduuuadd a99 as 0a a9",
                "ERROR  : Transf " + TEST_ID_4 + " aduuuadd a99 as 0a a9.."
        });
        //Loads the transformations and links them with the errors
        ArrayList<TransformationInfo> infos = new ArrayList<>(TransformationInfo.fromTransformations(t, errors));
        //Check that the errors are OK
        assertEquals(2, infos.get(0).getLogMessages().size());
        assertEquals(1, infos.get(1).getLogMessages().size());
    }

    /**
     * Test the from transformations with errors
     */
    @Test
    public void testFromTransformations_WithErrors(@Mocked final JsonHeaderInput anyHeader) throws JSONException {
        InputProgram p = new MockInputProgram();
        JSONObject obj = createTransformationsJSONObjectWithErrors(p);
        //Read the transformations
        InputStreamReader r = new InputStreamReader(
                new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8)));
        JsonSosiesInput input = new JsonSosiesInput(r, p);
        Collection<Transformation> infos = input.read();
        assertEquals(2, TransformationInfo.fromTransformations(infos, input.getLoadMessages()).size());
    }

    /**
     * Test the proper calculation of strength
     */
    @Test
    public void testStrength() {
        List<Transformation> t = TestHelpers.createTransformations(new MockInputProgram());
        ArrayList<TransformationInfo> infos = new ArrayList<>(
                TransformationInfo.fromTransformations(t, new ArrayList<String>()));
        infos.get(0).getTransplants().get(0).setClassification("F1", 5);
        infos.get(0).getTransplants().get(0).setClassification("F2", 3);
        infos.get(0).getTransplants().get(1).setClassification("F1", -5);
        assertTrue(infos.get(0).strength() - 3.0 < 00000000000000000.1);
    }


    private void assertInfosWhereCreatedProperly(ArrayList<TransformationInfo> infos) {
        assertEquals(2, infos.size());
        assertEquals(3, infos.get(0).getTransplants().size());
        assertEquals(1, infos.get(1).getTransplants().size());

        //Assert the transplant where properly loaded
        assertEquals("add", infos.get(0).getTransplants().get(0).getType());
        assertEquals("replace", infos.get(0).getTransplants().get(1).getType());
        assertEquals("delete", infos.get(0).getTransplants().get(2).getType());

        //Assert transformations where properly set
        assertNotNull(infos.get(0).getTransplants().get(0).getTransformation());
        assertEquals(ASTAdd.class, infos.get(0).getTransplants().get(0).getTransformation().getClass());

        assertNotNull(infos.get(0).getTransplants().get(2).getTransformation());
        assertEquals(ASTDelete.class, infos.get(0).getTransplants().get(2).getTransformation().getClass());

        assertNotNull(infos.get(1).getTransplants().get(0).getTransformation());
        assertEquals(ASTReplace.class, infos.get(1).getTransplants().get(0).getTransformation().getClass());
    }
}
