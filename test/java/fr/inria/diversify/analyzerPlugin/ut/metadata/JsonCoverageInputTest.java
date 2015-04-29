package fr.inria.diversify.analyzerPlugin.ut.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonClassificationInput;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonClassificationOutput;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonCoverageInput;
import fr.inria.diversify.analyzerPlugin.model.metadata.JsonCoverageOutput;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.input.JsonSosiesInputForUT;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import junit.framework.Assert;
import org.json.JSONObject;
import org.junit.Test;

import java.util.*;

import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfos;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createTransformations;
import static fr.inria.diversify.analyzerPlugin.TestHelpers.createInfosWithCoverage;
import static fr.inria.diversify.diversification.InputConfiguration.LATEST_GENERATOR_VERSION;
import static fr.inria.diversify.ut.json.SectionTestUtils.*;
import static fr.inria.diversify.ut.json.output.JsonHeaderOutputTest.SRC_POM;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 17/02/2015.
 */
public class JsonCoverageInputTest {

    /**
     * Test the normal output of the coverage information
     */
    @Test
    public void testReadCoverage() {

        //Create the data
        InputProgram p = new MockInputProgram();
        List<Transformation> transformations = createTransformations(p);
        ArrayList<TransformationInfo> ts = createInfosWithCoverage(transformations);

        //Write the data
        JsonSosieOutputForUT sosiesOutput = new JsonSosieOutputForUT(transformations, "/uzr/h0m3/f.jzon",
                SRC_POM, LATEST_GENERATOR_VERSION);
        sosiesOutput.setSection(JsonCoverageOutput.class, new JsonCoverageOutput(ts));
        JSONObject out = sosiesOutput.writeToJsonNow();

        //Read the data
        JsonSosiesInput input = new JsonSosiesInputForUT(getReaderFromJson(out), p);
        JsonCoverageInput sectionInput = new JsonCoverageInput(new ArrayList<TransformationInfo>());
        sectionInput.setJsonObject(out);
        input.setSection(JsonCoverageInput.class, sectionInput);
        input.read();

        assertEquals(0, input.getLoadMessages().size());

        Map<UUID, TransplantInfo> ti = sectionInput.getTransplantInfos();
        assertEquals(4, ti.size());


        TransformationInfo t1 = ti.get(TEST_ID_1).getTransplantationPoint();
        TransformationInfo t2 = ti.get(TEST_ID_4).getTransplantationPoint();
        //Assert
        assertEquals(2, t1.getTests().size());
        assertEquals(1, t2.getTests().size());
    }

}
