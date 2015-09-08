package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;

import javax.swing.*;

/**
 * Created by marodrig on 18/08/2015.
 */
public class DefaultClassificationProperties implements ClassificationProperties {

    private TestEyeProjectComponent component;

    private TransformClassifier classifier;

    @Override
    public void setClassifier(TransformClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public TransformClassifier getClassifier() {
        return classifier;
    }

    @Override
    public TestEyeProjectComponent getComponent() {
        return component;
    }

    @Override
    public void setComponent(TestEyeProjectComponent component) {
        this.component = component;
    }

}
