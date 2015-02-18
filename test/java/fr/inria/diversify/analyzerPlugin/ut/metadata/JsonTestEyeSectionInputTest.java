package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.TestHelpers;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonTestEyeSectionInput;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.input.JsonSosiesInputForUT;
import org.json.JSONObject;
import org.junit.Test;

import static fr.inria.diversify.ut.json.SectionTestUtils.TEST_ID_1;
import static fr.inria.diversify.ut.json.SectionTestUtils.TEST_ID_2;
import static fr.inria.diversify.ut.json.SectionTestUtils.getReaderFromJson;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 18/02/2015.
 */
public class JsonTestEyeSectionInputTest {

    /**
     * Test the normal reading of transformation infos from a descendant of JsonTestEyeSectionInput
     */
    @Test
    public void testRead() {
        InputProgram p = new MockInputProgram();
        JSONObject o = TestHelpers.createTransformationsJSON(p);

        JsonSosiesInput input = new JsonSosiesInputForUT(getReaderFromJson(o), p);
        JsonTestEyeSectionInput ti = new JsonTestEyeSectionInput();
        input.setSection(JsonTestEyeSectionInput.class, ti);
        input.read();

        assertEquals(4, ti.getTransplantInfos().size());
        assertNotNull(ti.getTransplantInfos().get(TEST_ID_1));
        assertNotNull(ti.getTransplantInfos().get(TEST_ID_2));
    }
}
