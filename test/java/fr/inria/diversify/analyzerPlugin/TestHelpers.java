package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.ut.json.output.JsonSosieOutputForUT;
import org.json.JSONObject;

import java.util.List;

import static fr.inria.diversify.ut.json.SectionTestUtils.list;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestHelpers {

    /**
     * Create a valid JSON file out of some transformations
     * @param p
     * @return
     */
    public static JSONObject createTransformationsJSON(InputProgram p) {
        JsonSosieOutputForUT s = new JsonSosieOutputForUT(createTransformations(p), "");
        return s.writeToJsonNow();
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
