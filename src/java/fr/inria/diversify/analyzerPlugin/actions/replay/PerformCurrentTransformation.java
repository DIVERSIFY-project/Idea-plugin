package fr.inria.diversify.analyzerPlugin.actions.replay;

import fr.inria.diversify.analyzerPlugin.MainToolWinv0;
import fr.inria.diversify.analyzerPlugin.actions.WinAction;
import fr.inria.diversify.analyzerPlugin.actions.searching.SeekCodeTransformation;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by marodrig on 03/11/2014.
 */
public class PerformCurrentTransformation extends WinAction {

    private static String TEMP_MOD = "_mod";

    String pomPath;


    String srcDir;

    public PerformCurrentTransformation(MainToolWinv0 mainToolWin, String pomPath, String srcDir) {
        super(mainToolWin);
        this.pomPath = pomPath;
        this.srcDir = srcDir;
    }

    @Override
    public void execute() {
        CodePosition data = getDataOfTransformationTree();
        if (data != null && data instanceof TransplantInfo) {
            //The intelligible code of the casting cast of the "castation".
            //Anyway,  we get here the parent node...
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode)
                    getMainToolWin().getTreeTransformations().getLastSelectedPathComponent()).getParent();

            //And here the transplantation point of the current transplant
            TransformationInfo tp = (TransformationInfo) parentNode.getUserObject();

            //obtain transplant we want to apply
            TransplantInfo transplant = (TransplantInfo) data;

            try {
                getMainToolWin().getTransplantTransformation(transplant, pomPath, srcDir);
                //Applies or restores the transformation
                tp.switchTransformation(transplant, srcDir, srcDir + TEMP_MOD);
                new SeekCodeTransformation(getMainToolWin(), tp, false).execute();
            } catch (Exception e) {
                complain("Cannot apply!! Something went wrong + " + e.getMessage(), e);
            }
        }
    }
}
