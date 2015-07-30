package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;

/**
 * Created by marodrig on 30/07/2015.
 */
public class ClassificationProperties {

    public TestEyeProjectComponent getComponent() {
        return component;
    }

    private TestEyeProjectComponent component;

    public ClassificationProperties() {
    }

    public void setComponent(TestEyeProjectComponent component) {
        this.component = component;
    }
}
