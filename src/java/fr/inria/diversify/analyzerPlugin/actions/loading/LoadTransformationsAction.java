package fr.inria.diversify.analyzerPlugin.actions.loading;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowErrorsAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowTransformationsInTree;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import com.intellij.openapi.diagnostic.Logger;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import icons.TestEyeIcons;

import java.io.*;
import java.util.ArrayList;

/**
 * Action to load transformations from file
 * <p/>
 * Created by marodrig on 26/01/2015.
 */
public class LoadTransformationsAction extends TestEyeAction {

    private static final Logger logger = Logger.getInstance("#" + LoadTransformationsAction.class.getName());

    public LoadTransformationsAction() {
        super("Load transformations", "Load transformations", TestEyeIcons.Open);
    }

    protected String userSelectsFile(boolean directory) throws FileNotFoundException {
        //Shows a window to load the file
        FileChooserDescriptor f = new FileChooserDescriptor(!directory, directory, false, false, false, false);
        //Get the file path
        VirtualFile fv = FileChooser.chooseFile(f, null, null);
        return fv == null ? null : fv.getCanonicalPath();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Returns if the user cancels
        try {
            TestEyeProjectComponent c = getComponent(e);
            logger.info("Component: " + c.toString());

            String path = userSelectsFile(false);
            if ( path == null ) return;
            logger.info("User selected path: " + c.toString());

            //Load the transformations
            logger.info("Program source code dir: " + c.getProgramSourceCodeDir());
            logger.info("Program base dir: " + c.getProgram().getProgramDir());

            c.loadInfos(path);
            logger.info("Transformations loaded: " + c.getInfos().size());

            tryExecute(ShowTransformationsInTree.class, e);
            tryExecute(ShowErrorsAction.class, e);
            logger.info("Transformations presented!");
        } catch (Exception ex) {
            hardComplain("Unable to load transformations", ex);
            tryExecute(ShowErrorsAction.class, e);
        }
    }
}
