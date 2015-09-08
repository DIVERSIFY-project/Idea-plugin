package fr.inria.diversify.analyzerPlugin.actions.reporting;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.io.DialogSavePathProvider;
import fr.inria.diversify.analyzerPlugin.model.io.SavePathProvider;
import fr.inria.diversify.analyzerPlugin.model.io.TransplantLatexExporter;
import icons.TestEyeIcons;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by marodrig on 25/08/2015.
 */
public class SaveLatexAction extends TestEyeAction {


    private final CodePositionTree tree;
    private final SavePathProvider pathProvider;

    public SaveLatexAction(CodePositionTree treeTransformations, @Nullable SavePathProvider pathProvider) {
        super("Export latex snippet ", "Export latex snippet ", TestEyeIcons.Latex);
        this.tree = treeTransformations;
        this.pathProvider = pathProvider == null ? new DialogSavePathProvider() : pathProvider;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        CodePosition cp = tree.getSelectedCodePosition();
        if (!(cp instanceof TransplantInfo)) {
            softComplain(tree, "Select a transplant", null);
            return;
        }
        TransplantInfo info = (TransplantInfo) cp;
        TransplantLatexExporter exporter = new TransplantLatexExporter();
        try {
            Path p = pathProvider.getPath();
            String fileName = p.getFileName().toString();
            String s = p.toString();
            if (!s.isEmpty()) exporter.export(s, info, "Some comments", fileName);
        } catch (IOException e) {
            hardComplain("Cannot export", e);
        }

    }
}
