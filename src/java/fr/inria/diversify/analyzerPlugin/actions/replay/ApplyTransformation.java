package fr.inria.diversify.analyzerPlugin.actions.replay;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import icons.TestEyeIcons;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent.*;

/**
 * Created by marodrig on 03/11/2014.
 */
public class ApplyTransformation extends TestEyeAction {

    private final CodePositionTree tree;

    String pomPath;

    String srcDir;

    public ApplyTransformation(CodePositionTree tree) {
        super("Switch Transplant", "Switch transplant", TestEyeIcons.ReplaceTransformation);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            CodePosition data = tree.getSelectedCodePosition();
            if (data != null && data instanceof TransplantInfo) {
                TestEyeProjectComponent c = getComponent(event);

                String basePath = event.getProject().getBasePath();
                String srcPath = c.getProgram().getSourceCodeDir();
                //obtain transplant we want to apply
                TransplantInfo transplant = (TransplantInfo) data;
                TransformationInfo tp = transplant.getTransplantationPoint();

                //Applies or restores the transformation
                tp.switchTransformation(transplant, srcPath, basePath + TEMP_MOD);

                TreePath paths = tree.getSelectionPath();
                tree.setSelectionPath(paths.getParentPath());
                tryExecute(SeekCodeTransformation.class, event);
            }
        } catch (Exception e) {
            hardComplain("Cannot apply! Something went wrong!", e);
        }
    }
}
