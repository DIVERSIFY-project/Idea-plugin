package fr.inria.diversify.analyzerPlugin.actions.loading;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import fr.inria.diversify.analyzerPlugin.MainToolWin;
import fr.inria.diversify.analyzerPlugin.actions.Complain;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.diversification.InputProgram;

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * Action to load transformations from file
 *
 * Created by marodrig on 26/01/2015.
 */
public class LoadTransformationsAction extends WinAction {

    private final InputProgram program;

    private Project project;

    public LoadTransformationsAction(MainToolWin mainToolWin, Project project, InputProgram program) {
        super(mainToolWin);
        this.project = project;
        this.program = program;
    }

    protected String userSelectsFile(boolean directory) throws FileNotFoundException {
        //Shows a window to load the file
        FileChooserDescriptor f = new FileChooserDescriptor(!directory, directory, false, false, false, false);
        //Get the file
        VirtualFile fv = FileChooser.chooseFile(f, project, null);
        if (fv == null) throw new FileNotFoundException("Can't found " + fv.getCanonicalPath());
        return fv.getCanonicalPath();
    }

    @Override
    public void execute() {
        //Returns if the user cancels
        try {
            Collection<TransformationInfo> infos = TransformationInfo.fromJSON(userSelectsFile(false), program);
            new ShowTransformationsInTree(
                    getMainToolWin(), infos, getMainToolWin().getTreeTransformations()).execute();
        } catch (Exception e) {
            new Complain(getMainToolWin(), "Unable to load transformations", e, false).execute();
        }
    }
}
