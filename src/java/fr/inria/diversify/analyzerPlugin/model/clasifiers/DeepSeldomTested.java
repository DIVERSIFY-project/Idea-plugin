package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

/**
 * Created by marodrig on 30/07/2015.
 */
public  class DeepSeldomTested extends DepthNumberOfTests {

    @Override
    public boolean isUserFilter() {
        return true;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform)
    {
        double md = transform.getTransplantationPoint().getMeanDepth();
        TestEyeProjectComponent c = getProperties().getComponent();
        return  ( md > c.getMeanDepth() &&  transform.getTransplantationPoint().getTests().size() < c.getMeanNumberOfTest() );
    }


    @Override
    protected int calculateValue(TransplantInfo transform) {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Deep sosies seldom tested";
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
