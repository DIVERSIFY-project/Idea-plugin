package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import com.intellij.openapi.actionSystem.AnActionEvent;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Interface to properties of the classifiers
 *
 * Created by marodrig on 30/07/2015.
 */
public interface ClassificationProperties {

    public void setClassifier(TransformClassifier classifier);

    public TransformClassifier getClassifier();

    public TestEyeProjectComponent getComponent();

    public void setComponent(TestEyeProjectComponent component);
}
