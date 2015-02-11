package fr.inria.diversify.analyzerPlugin.actions.display;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.CodePositionTree;
import fr.inria.diversify.analyzerPlugin.gui.EnhancedCoverageTree;
import fr.inria.diversify.analyzerPlugin.model.CodePosition;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by marodrig on 03/02/2015.
 */
public class ShowCoverageInfo extends TestEyeAction {

    public static final java.lang.String ID = "TestEye." + ShowCoverageInfo.class.getSimpleName();
    /**
     * Coverage tree where the coverage is being set
     */
    private final EnhancedCoverageTree covTree;

    /**
     * Code position to extract the coverage belonging to the selected coverage
     */
    private final CodePositionTree codeTree;

    public ShowCoverageInfo(CodePositionTree codeTree, EnhancedCoverageTree covTree) {
        this.codeTree = codeTree;
        this.covTree = covTree;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            CodePosition cp = codeTree.getSelectedCodePosition();
            if ( cp != null ) {
                if (cp instanceof TransformationInfo)
                    covTree.showCoverage((TransformationInfo) cp);
                else if (cp instanceof TransplantInfo) {
                    TransformationInfo info = ((TransplantInfo) cp).getTransplantationPoint();
                    covTree.showCoverage(info);
                }
            }
        } catch (Exception e) {
            hardComplain("Can't show coverage", e);
        }
    }
}
