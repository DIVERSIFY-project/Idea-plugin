package fr.inria.diversify.analyzerPlugin.actions.searching;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import fr.inria.diversify.analyzerPlugin.actions.TestEyeAction;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.gui.FilterPanel;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;

import javax.swing.*;
import java.awt.*;

/**
 * Executes the configuration action for a classifier
 *
 * Created by marodrig on 18/08/2015.
 */
public class ShowClassifierProperties extends TestEyeAction {

    private final Class<? extends TransformClassifier> classifierClass;

    public ShowClassifierProperties(Class<? extends TransformClassifier> classifierClass) {
        super("Classification properties", "Show classification properties", IconUtil.getAddIcon());
        this.classifierClass = classifierClass;
    }

    @Override
    public void actionPerformed(final AnActionEvent event) {
        TransformClassifier transformClassifier = getComponent(event).getClassiferByClass(classifierClass);
        if ( transformClassifier.getConfigureAction() != null ) {
            transformClassifier.getConfigureAction().actionPerformed(event);
        }
    }
}
