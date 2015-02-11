package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.TreeTransformations;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

import javax.swing.*;

/**
 * Given a node of JTree containing a Code position, this action will navigate to that position
 *
 * Created by marodrig on 03/11/2014.
 */
public class SeekCodeTransformation extends TestEyeAction {


    private boolean includeMethodName;

    private CodePositionTree tree;

    /**
     * Position to travel to
     */
    private CodePosition codePosition;

    public SeekCodeTransformation(CodePositionTree tree) {
        super("Seek code", "Load transformations", IconUtil.getAddFolderIcon());
        this.tree = tree;
    }



    @Override
    public void actionPerformed(AnActionEvent event) {

        //Get the Tranformation's tree thanks tho the data context magic in IntelliJ IDEA framework
        //CodePositionTree tree = event.getData(CodePositionTree.TEST_EYE_CODE_POSITION_TREE);

        CodePosition data = tree.getSelectedCodePosition();

        Project project = event.getProject();

        if (data == null) return;

        String[] p = data.getPosition().split(":");
        String className = p[0];
        if (includeMethodName) {
            className = className.substring(0, className.lastIndexOf('.'));
        }

        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(className, scope);

        if (psiClass != null) {
            //FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            //FileEditor[] fe = fileEditorManager.openFile(vf, true, true);

            //Open the file containing the transformation
            VirtualFile vf = psiClass.getContainingFile().getVirtualFile();
            vf.refresh(false, false);
            //Jump there
            int line = Integer.parseInt(p[1]);
            line = line > 1 ? line - 1 : line;
            new OpenFileDescriptor(project, vf, line, 0).navigateInEditor(project, false);

        } else {
            softComplain(tree, "I was unable to find the class corresponding to the transformation :( ...\n" +
                    "Do the transformation file belongs to this project?", null);
        }
    }
}
