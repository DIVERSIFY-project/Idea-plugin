package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.model.AssertInfo;
import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.ut.MockInputProgram;
import fr.inria.diversify.ut.json.SectionTestUtils;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import mockit.Expectations;
import mockit.Verifications;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.inria.diversify.ut.json.SectionTestUtils.*;
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
    public static ArrayList<TransformationInfo> createInfos() {
        return createInfos(createTransformations(new MockInputProgram()));
    }

    /**
     * Creates an Array list of Transformation info
     *
     * @return An array of transformation info
     */
    public static ArrayList<TransformationInfo> createInfos(Collection<Transformation> ts) {
        return new ArrayList<TransformationInfo>(TransformationInfo.fromTransformations(ts, new ArrayList<String>()));
    }

    /**
     * Creates an Array list of Transformation info with coverage information
     *
     * @return An array of transformation info
     */
    public static ArrayList<TransformationInfo> createInfosWithCoverage(List<Transformation> ts) {
        ArrayList<TransformationInfo> infos = new ArrayList<>(
                TransformationInfo.fromTransformations(ts, new ArrayList<String>()));

        TestInfo t1 = new TestInfo("org.MyClassTest:10");
        TestInfo t2 = new TestInfo("org.MyOtherClassTest:100");
        t1.getAsserts().add(new AssertInfo("org.MyClassTest:20"));
        t1.getAsserts().add(new AssertInfo("org.MyClassTest:22"));
        t2.getAsserts().add(new AssertInfo("org.MyClassTest:105"));
        infos.get(0).getTests().put(t1, new PertTestCoverageData(t1, 1, 3));
        infos.get(0).getTests().put(t2, new PertTestCoverageData(t2, 5, 3));
        infos.get(1).getTests().put(t2, new PertTestCoverageData(t2, 3, 2));

        return infos;
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
        add.setIndex(TEST_ID_1);
        add.setTransplantationPoint(p.getCodeFragments().get(2));
        add.setTransplant(p.getCodeFragments().get(1));

        ASTReplace r1 = new ASTReplace();
        r1.setIndex(TEST_ID_2);
        r1.setTransplantationPoint(p.getCodeFragments().get(2));
        r1.setTransplant(p.getCodeFragments().get(1));

        ASTDelete del = new ASTDelete();
        del.setIndex(TEST_ID_3);
        del.setTransplantationPoint(p.getCodeFragments().get(2));

        ASTReplace r = new ASTReplace();
        r.setIndex(TEST_ID_4);
        r.setTransplantationPoint(p.getCodeFragments().get(1));
        r.setTransplant(p.getCodeFragments().get(2));

        return list(add, r1, del, r);
    }
}
