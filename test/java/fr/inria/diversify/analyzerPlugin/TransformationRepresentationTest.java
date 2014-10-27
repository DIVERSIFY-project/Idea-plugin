package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.analyzerPlugin.model.TransformationRepresentation;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by marodrig on 04/09/2014.
 */
public class TransformationRepresentationTest {

    private JSONObject createSnippet(String source, String pos) throws JSONException {
        JSONObject snippet = new JSONObject();
        snippet.put("sourceCode", source);
        snippet.put("position", pos);
        snippet.put("type", "CtLocalVariableImpl");
        return snippet;
    }

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
    public void testFromJSON() throws JSONException {
        JSONObject s1 = createSnippet("the source code", "the.position:33");
        JSONObject s2 = createSnippet("the other source", "the.other.position:56");
        JSONObject transformation = createTransformation(s1, s2);

        TransformationRepresentation representation = new TransformationRepresentation();
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
    public void testFromJSONDelete() throws JSONException {
        JSONObject snippet = createSnippet("the source code", "the.position:33");
        JSONObject transformation = createTransformation(snippet, null);

        TransformationRepresentation representation = new TransformationRepresentation();
        representation.fromJSONObject(transformation, new JSONObject());
        assertEquals("the.position:33", representation.getPosition());
        assertEquals("replace", representation.getType());
    }

}
