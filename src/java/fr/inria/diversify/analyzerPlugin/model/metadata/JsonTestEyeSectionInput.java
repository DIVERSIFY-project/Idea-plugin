package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.persistence.json.input.JsonSectionInput;
import fr.inria.diversify.transformation.Transformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by marodrig on 18/02/2015.
 */
public class JsonTestEyeSectionInput extends JsonSectionInput {

    private HashMap<UUID, TransplantInfo> transplantInfos;

    public HashMap<UUID, TransplantInfo> getTransplantInfos() {
        if ( transplantInfos == null ) transplantInfos = new HashMap<>();
        return transplantInfos;
    }

    public void setTransplantInfos(HashMap<UUID, TransplantInfo> transplantInfos) {
        this.transplantInfos = transplantInfos;
    }

    protected void readTransformationsInfos(HashMap<UUID, Transformation> transformations) {
        Collection<TransformationInfo> ts = TransformationInfo.fromTransformations(transformations.values(), getLoadMessages());
        for (TransformationInfo ti : ts) {
            for (TransplantInfo tp : ti.getTransplants()) {
                getTransplantInfos().put(tp.getTransformation().getIndex(), tp);
            }
        }
    }

    @Override
    public void read(HashMap<UUID, Transformation> transformations) {
        if ( getTransplantInfos().size() == 0 ) {
            readTransformationsInfos(transformations);
        }
    }
}
