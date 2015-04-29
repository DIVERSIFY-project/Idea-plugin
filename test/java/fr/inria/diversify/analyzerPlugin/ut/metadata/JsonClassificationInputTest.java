package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonClassificationInput;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonClassificationOutput;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.json.input.JsonSectionInput;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.input.JsonHeaderInputTest;
import fr.inria.diversify.ut.json.input.JsonSosiesInputForUT;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.*;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfos;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static fr.inria.diversify.diversification.InputConfiguration.LATEST_GENERATOR_VERSION;
import static fr.inria.diversify.ut.json.SectionTestUtils.*;
import static fr.inria.diversify.ut.json.output.JsonHeaderOutputTest.SRC_POM;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 17/02/2015.
 */
public class JsonClassificationInputTest {

    /**
     * Test the normal output of the tags information
     */
    @Test
    public void testReadTags() throws JSONException {

        //Create the data
        InputProgram p = new MockInputProgram();
        List<Transformation> transformations = createTransformations(p);
        ArrayList<TransformationInfo> ts = createInfos(transformations);
        ts.get(0).getTransplants().get(0).setTags("weak");
        ts.get(0).getTransplants().get(1).setTags("strong");
        ts.get(1).getTransplants().get(0).setTags("crazy");

        //Write the data
        JsonSosieOutputForUT sosiesOutput = new JsonSosieOutputForUT(transformations, "/uzr/h0m3/f.jzon",
                SRC_POM, LATEST_GENERATOR_VERSION);
        sosiesOutput.setSection(JsonClassificationOutput.class, new JsonClassificationOutput(ts));
        JSONObject out = sosiesOutput.writeToJsonNow();

        //Read the data
        JsonSosiesInput input = new JsonSosiesInputForUT(getReaderFromJson(out), p);
        JsonClassificationInput sectionInput = new JsonClassificationInput(new ArrayList<TransformationInfo>());
        sectionInput.setJsonObject(out);
        input.setSection(JsonClassificationInput.class, sectionInput);
        input.read();

        //Assert
        Map<UUID, TransplantInfo> ti = sectionInput.getTransplantInfos();
        assertEquals("weak", ti.get(TEST_ID_1).getTags());
        assertEquals("strong", ti.get(TEST_ID_2).getTags());
        assertEquals("crazy",  ti.get(TEST_ID_4).getTags());
    }

}
