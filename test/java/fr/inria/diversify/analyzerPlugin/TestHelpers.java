package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import mockit.Expectations;
import mockit.Verifications;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static fr.inria.diversify.ut.json.SectionTestUtils.list;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestHelpers {

    /**
     * Creates an Array list of Transformation info
     *
     * @return An array of transformation info
     */
    public static ArrayList<TransformationInfo> getInfos() {
        return new ArrayList<>(
                TransformationInfo.fromTransformations(
                        createTransformations(new MockInputProgram()),
                        new ArrayList<String>()));
    }

    /**
     * Verify tht a complain action was called
     */
    public static void verifyHardComplain() {
        new Verifications() {{
            JOptionPane.showMessageDialog(null, anyString, anyString, JOptionPane.ERROR_MESSAGE);
        }};
    }

    /**
     * Mock the Show messages dialog
     *
     * @return
     */
    public static Expectations expectHardComplain() {
        return new Expectations() {{
            JOptionPane.showMessageDialog(null, anyString, anyString, JOptionPane.ERROR_MESSAGE);
        }};
    }

    /**
     * Create a valid JSON file out of some transformations
     *
     * @param p
     * @return
     */
    public static JSONObject createTransformationsJSON(InputProgram p) {
        JsonSosieOutputForUT s = new JsonSosieOutputForUT(createTransformations(p), "", "", "");
        return s.writeToJsonNow();
    }

    /**
     * Assert that an action was called by another
     *
     * @param caller      Calling action
     * @param calledClass Called action class
     * @param count       number of times
     */
    public static void assertActionCalled(TestEyeAction caller, Class<?> calledClass, int count) {
        assertEquals(count, (int) ((FakeIDEObjects) caller.getIdeObjects()).tryExecuteCount.get(calledClass.getName()));
    }

    /**
     * Creates a collection of transformations that matches the fake fragments of the mock program
     *
     * @param p
     * @return
     */
    public static List<Transformation> createTransformations(InputProgram p) {
        ASTAdd add = new ASTAdd();
        add.setIndex(0);
        add.setTransplantationPoint(p.getCodeFragments().get(2));
        add.setTransplant(p.getCodeFragments().get(1));

        ASTReplace r1 = new ASTReplace();
        r1.setIndex(1);
        r1.setTransplantationPoint(p.getCodeFragments().get(2));
        r1.setTransplant(p.getCodeFragments().get(1));

        ASTDelete del = new ASTDelete();
        del.setIndex(2);
        del.setTransplantationPoint(p.getCodeFragments().get(2));

        ASTReplace r = new ASTReplace();
        r.setIndex(3);
        r.setTransplantationPoint(p.getCodeFragments().get(1));
        r.setTransplant(p.getCodeFragments().get(2));

        return list(add, r1, del, r);
    }
}
