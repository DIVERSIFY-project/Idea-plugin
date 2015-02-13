package fr.inria.diversify.analyzerPlugin.actions.loading;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowCoverageInfo;
import fr.inria.diversify.analyzerPlugin.actions.display.ShowErrorsAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageEntryFactory;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageProcessor;
import fr.inria.diversify.analyzerPlugin.model.metadata.EnhancedCoverageReader;
import fr.inria.diversify.analyzerPlugin.model.metadata.SyringeDataReader;
import icons.TestEyeIcons;

import java.io.FileNotFoundException;

/**
 * Action to load transformations from file
 * <p/>
 * Created by marodrig on 26/01/2015.
 */
public class LoadEnhancedCoverageAction extends TestEyeAction {

    private static final Logger logger = Logger.getInstance("#" + LoadEnhancedCoverageAction.class.getName());

    public LoadEnhancedCoverageAction() {
        super("Load enhanced coverage log", "Load enhanced coverage log", TestEyeIcons.OpenLog);
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
            String path = userSelectsFile(true);
            if ( path == null ) return;

            //Load the coverage data into the transformations info
            EnhancedCoverageReader reader = new EnhancedCoverageReader(c.getInfos());
            reader.read("augmentedCoverage.id", path);

            //Show them
            tryExecute(ShowCoverageInfo.class, e);
            //The errors to
            c.getLogMessages().addAll(reader.getProcessingErrors());
            tryExecute(ShowErrorsAction.class, e);
        } catch (Exception ex) {
            hardComplain("Unable to load transformations", ex);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        setEnabledInModalContext(false);
    }
}
