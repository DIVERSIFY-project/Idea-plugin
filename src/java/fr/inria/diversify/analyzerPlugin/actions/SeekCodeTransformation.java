package fr.inria.diversify.analyzerPlugin.actions;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;

import javax.swing.*;

/**
 * Created by marodrig on 03/11/2014.
 */
public class SeekCodeTransformation extends WinAction {

    private boolean includeMethodName;
    private CodePosition codePosition;

    public SeekCodeTransformation(MainToolWin toolWin, CodePosition cp, boolean includeMethodName) {
        super(toolWin);
        this.includeMethodName = includeMethodName;
        this.codePosition = cp;
    }

    @Override
    public void execute() {
        CodePosition data = codePosition;
        Project project = getMainToolWin().getProject();

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
            JOptionPane.showMessageDialog(getMainToolWin().getPanelContent(),
                    "I was unable to find the class corresponding to the transformation :( ...\n" +
                            "Do the transformation file belongs to this project?",
                    "Ups...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
