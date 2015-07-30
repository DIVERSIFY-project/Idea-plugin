package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.model.PertTestCoverageData;
import fr.inria.diversify.analyzerPlugin.model.TestInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marodrig on 30/07/2015.
 */
public abstract class DepthNumberOfTests extends TransformClassifier {

    protected double medianDepth(TransplantInfo transplantInfo) {
        int md = 0;
        for (Map.Entry<TestInfo, PertTestCoverageData> t :
                transplantInfo.getTransplantationPoint().getTests().entrySet() ) {
            md += t.getValue().getMeanDepth();
        }
        return md / transplantInfo.getTransplantationPoint().getTests().size();
    }

    @Override
    public boolean isUserFilter() {
        return false;
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return 1;
    }

    @Override
    public int getWeight() {
        return 1;
    }
}
