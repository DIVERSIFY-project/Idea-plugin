package fr.inria.diversify.analyzerPlugin.actions.reporting;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.io.DialogSavePathProvider;
import fr.inria.diversify.analyzerPlugin.model.io.SavePathProvider;
import fr.inria.diversify.analyzerPlugin.model.io.TransplantLatexExporter;
import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import icons.TestEyeIcons;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by marodrig on 25/08/2015.
 */
public class OneShotSaveLatexAction extends TestEyeAction {


    private final CodePositionTree tree;
    private final SavePathProvider pathProvider;

    private void saveTPToLatex(TestEyeProjectComponent component, String tpPosition,
                               int expectedTransplants, String type, String quality) throws IOException {
        saveTPToLatex(component, tpPosition, expectedTransplants, type, quality, "");
    }

    /**
     * Save to latex the transplantation points in that transplant.
     *
     * @param component           TestEyeComponent to obtain the data from
     * @param comment             Comment given to the transplant in case there is only one
     * @param tpPosition          Transplant position
     * @param expectedTransplants Expected number of transplants of a given type in that position
     * @param type                Type of the trasnplants
     * @param quality             User determined quality of the transplants
     */
    private void saveTPToLatex(TestEyeProjectComponent component, String tpPosition,
                               int expectedTransplants, String type, String quality, String comment) throws IOException {
        comment = comment.trim();
        tpPosition = tpPosition.trim();
        type = type.toLowerCase().trim();
        quality = quality.toLowerCase().trim();

        List<TransformationInfo> infos = component.getVisibleInfos();
        for (TransformationInfo i : infos) {
            if (i.getPosition().equals(tpPosition)) {
                //Make sure that the number of elements of a given type is equal to the expected one
                int numberOfTpOfType = 0;
                for (TransplantInfo tinfo : i.getTransplants()) {
                    if (tinfo.getVisibility() == TransplantInfo.Visibility.show
                            && tinfo.getType().toLowerCase().contains(type)) {
                        numberOfTpOfType++;
                    }
                }
                //If it is different we can't do anything because we don't know which one is
                if (numberOfTpOfType != expectedTransplants)
                    System.out.println("Cannot save, numbers mismatch. Expected: "
                            + expectedTransplants + " found: " + numberOfTpOfType );
                else {
                    for (TransplantInfo tinfo : i.getTransplants()) {
                        if (tinfo.getType().toLowerCase().contains(type)) {
                            TransplantLatexExporter exporter = new TransplantLatexExporter();
                            CodeFragment ctTP = ((ASTTransformation) tinfo.getTransformation()).getTransplantationPoint();
                            CtMethod m = ctTP.getCtCodeFragment().getParent(CtMethod.class);
                            CtClass c = ctTP.getCtCodeFragment().getParent(CtClass.class);
                            String methodName = m == null ? "" : m.getSimpleName();
                            String className = c == null ? "" : c.getSimpleName();
                            String id = quality + "-" + type + "-sosie-" + className + "-" + methodName;
                            String path = "C:\\MarcelStuff\\DATA\\DIVERSE\\new-dataset\\" + id;
                            String n = "";
                            int k = 0;
                            while (new File(path + n + ".tex").exists()) {
                                k++;
                                n = String.valueOf(k);
                            }

                            exporter.export(path + n + ".tex", tinfo, comment, id);
                        }
                    }
                }
            }
        }
    }

    public OneShotSaveLatexAction(CodePositionTree treeTransformations, @Nullable SavePathProvider pathProvider) {
        super("Export latex snippet ", "Export latex snippet ", TestEyeIcons.Latex);
        this.tree = treeTransformations;
        this.pathProvider = pathProvider == null ? new DialogSavePathProvider() : pathProvider;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            TestEyeProjectComponent c = getComponent(anActionEvent);
            /*
            saveTPToLatex(c, "com.google.gson.Gson:872", 1, "Add", "fooler");
            saveTPToLatex(c, "com.google.gson.JsonArray:47", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.JsonObject:101", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.JsonObject:55", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.Excluder:170", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:146", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:183", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:246", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:319", 1, "ADD", "BAD");
            saveTPToLatex(c, "com.google.gson.GsonBuilder:249,1", 1, "ADD", "BAD");
            saveTPToLatex(c, "com.google.gson.JsonObject:112", 1, "ADD", "BAD");
            saveTPToLatex(c, "com.google.gson.JsonArray:116", 1, "REPLACE", "BAD");
            saveTPToLatex(c, "com.google.gson.JsonPrimitive:90", 1, "REPLACE", "fooler");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:167", 1, "DELETE", "BAD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:339", 1, "DELETE", "BAD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:420", 1, "DELETE", "BAD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:422", 1, "DELETE", "BAD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:431", 1, "DELETE", "BAD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:105", 1, "DELETE", "GOOD", "The code overcleans");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:192", 1, "DELETE", "GOOD");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:361", 1, "DELETE", "GOOD", "OPTIMIZATION REMOVAL");
            saveTPToLatex(c, "com.google.gson.internal.LinkedHashTreeMap:385", 1, "DELETE", "GOOD", "OPTIMIZATION REMOVAL");
            */

            saveTPToLatex(c, "com.google.gson.reflect.TypeToken:280", 5 ,"REPLACE", "GOOD");
            saveTPToLatex(c, "com.google.gson.internal.$Gson$Types:94", 3, "Replaces", "good", "( All this removes code for special cases. I can't agree they are all good. However, in most casses they will work) ");
            saveTPToLatex(c, "com.google.gson.internal.$Gson$Types:95", 2, "Replaces", "good");
            saveTPToLatex(c, "com.google.gson.internal.$Gson$Types:96", 1, "Replaces", "good");
            saveTPToLatex(c, "com.google.gson.Gson:370", 1, "Replaces", "good", "Forces the cleanup");
            saveTPToLatex(c, "com.google.gson.Gson:370", 1, "DELETE", "GOOD");
            saveTPToLatex(c, "com.google.gson.Gson:371", 1, "DELETE", "GOOD", "Removes a cleanup");
            saveTPToLatex(c, "com.google.gson.Gson:363", 1, "REPLACE", "fooler", "");
            saveTPToLatex(c, "com.google.gson.Gson:362", 1, "DELETE", "GOOD", "Removes a caché");
            saveTPToLatex(c, "com.google.gson.Gson:361", 1, "ADD", "fooler");
            saveTPToLatex(c, "com.google.gson.Gson:360", 2, "ADD", "GOOD", "Removes a cache");
            saveTPToLatex(c, "com.google.gson.Gson:345", 1, "DELETE", "good");
            saveTPToLatex(c, "com.google.gson.Gson:345", 1, "REplace", "good");
            saveTPToLatex(c, "com.google.gson.Gson:345", 2, "Add", "good");
        } catch (Exception e) {
            hardComplain("Cannot save to latex", e);
        }
    }
}
