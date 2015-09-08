package fr.inria.diversify.analyzerPlugin.model.clasifiers;

import fr.inria.diversify.analyzerPlugin.actions.searching.classificationProperty.QueryExtendedCoveragePropertyAction;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by marodrig on 18/08/2015.
 */
public class QueryExtendedCoverage extends TransformClassifier {

    public static class CoverageProperties extends DefaultClassificationProperties {

        Set<String> transplantationPoints;

        private String dbFileName = "";

        public void setTransplantationPoints(Set<String> transplantationPoints) {
            this.transplantationPoints = transplantationPoints;
        }

        public Set<String> getIndexes() {
            if (transplantationPoints == null) transplantationPoints = new HashSet<>();
            return transplantationPoints;
        }

        public String getDBFileName() {
            return dbFileName;
        }

        public String getGetDBFileName() {
            return dbFileName;
        }

        public void setDBFileName(String dbFileName) {
            this.dbFileName = dbFileName;
        }

    }

    public QueryExtendedCoverage() {
        ClassificationProperties p = new CoverageProperties();
        p.setClassifier(this);
        setConfigureAction(new QueryExtendedCoveragePropertyAction());
        setProperties(p);
    }

    @Override
    public void beforeClassify() {
        super.beforeClassify();
    }

    @Override
    public void afterClassification() {
        super.afterClassification();
    }

    @Override
    public boolean isUserFilter() {
        return true;
    }

    @Override
    protected boolean canClassify(TransplantInfo transform) {
        CoverageProperties f = (CoverageProperties)getProperties();
        return f.getIndexes().contains(transform.getIndex().toString());
    }

    @Override
    protected int calculateValue(TransplantInfo transform) {
        return 1;
    }

    @Override
    public String getDescription() {
        return "(*) Query extended coverage";
    }

    @Override
    public int getWeight() {
        return 1;
    }

}
