package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

/**
 * Created by marodrig on 30/07/2015.
 */
public  class DeepFrequentlyTested extends DepthNumberOfTests {

    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform)
    {
        double md = medianDepth(transform);
        TestEyeProjectComponent c = getProperties().getComponent();
        return  ( md > c.getMeanDepth() &&  transform.getTransplantationPoint().getTests().size() > c.getMeanNumberOfTest() );
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Deep sosies frequently tested";
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
