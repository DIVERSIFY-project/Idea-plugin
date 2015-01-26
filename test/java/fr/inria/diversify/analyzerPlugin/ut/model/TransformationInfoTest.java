package fr.inria.diversify.analyzerPlugin.ut.model;

import fr.inria.diversify.analyzerPlugin.io.TransformationInfoFactory;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
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
import java.util.Collection;
import java.util.List;

import static fr.inria.diversify.ut.json.SectionTestUtils.createTransformations;
import static fr.inria.diversify.ut.json.SectionTestUtils.list;
import static org.junit.Assert.assertEquals;

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
        if ( transplant != null ) {
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
     */
    @Test
    public void testFromJson(@Mocked FileWriter anyWriter) {
        //Write the transformations
        InputProgram p = new MockInputProgram();
        List<Transformation> t = createTransformations(p);
        JsonSosieOutputForUT out = new JsonSosieOutputForUT(t, "/uzr/h0m3/my.jzon");
        out.write(); //We need to mock the File writer so no writing to file is done

        InputStreamReader r = new InputStreamReader(
                new ByteArrayInputStream(out.getJSONObject().toString().getBytes(StandardCharsets.UTF_8)));
        ArrayList<TransformationInfo> infos = new ArrayList<>(TransformationInfo.fromJSON(r, p));
        assertInfosWhereCreatedProperly(infos);
    }

    /**
     * Turns transformations into a plugin friendly data format
     */
    @Test
    public void testFromTransformations() {
        List<Transformation> t = createTransformations(new MockInputProgram());
        ArrayList<TransformationInfo> infos = new ArrayList<>(TransformationInfo.fromTransformations(t));
        assertInfosWhereCreatedProperly(infos);
    }

    private void assertInfosWhereCreatedProperly(ArrayList<TransformationInfo> infos) {
        assertEquals(2, infos.size());
        assertEquals(3, infos.get(0).getTransplants().size());
        assertEquals(1, infos.get(1).getTransplants().size());

        assertEquals("add", infos.get(0).getTransplants().get(0).getType());
        assertEquals("replace", infos.get(0).getTransplants().get(1).getType());
        assertEquals("delete", infos.get(0).getTransplants().get(2).getType());
    }

    /**
     * Creates a collection of transformations that matches the fake fragments of the mock program
     * @return
     * @param p
     */
    public static List<Transformation> createTransformations(InputProgram p) {
        ASTAdd add = new ASTAdd();
        add.setTransplantationPoint(p.getCodeFragments().get(2));
        add.setTransplant(p.getCodeFragments().get(1));

        ASTReplace r1 = new ASTReplace();
        r1.setTransplantationPoint(p.getCodeFragments().get(2));
        r1.setTransplant(p.getCodeFragments().get(1));

        ASTDelete del = new ASTDelete();
        del.setTransplantationPoint(p.getCodeFragments().get(2));

        ASTReplace r = new ASTReplace();
        r.setTransplantationPoint(p.getCodeFragments().get(1));
        r.setTransplant(p.getCodeFragments().get(2));

        return list(add, r1, del, r);
    }

}
