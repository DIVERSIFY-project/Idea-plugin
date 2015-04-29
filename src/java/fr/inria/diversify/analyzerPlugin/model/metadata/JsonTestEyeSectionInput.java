package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.persistence.json.input.JsonSectionInput;
import fr.inria.diversify.transformation.Transformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by marodrig on 18/02/2015.
 */
public class JsonTestEyeSectionInput extends JsonSectionInput {

    private Map<UUID, TransplantInfo> transplantInfos;

    private Collection<TransformationInfo> transformationInfos;

    public JsonTestEyeSectionInput(Collection<TransformationInfo> infos) {
        super();
        setTransformationInfos(infos);
    }

    public Map<UUID, TransplantInfo> getTransplantInfos() {
        if ( transplantInfos == null ) transplantInfos = new HashMap<>();
        return transplantInfos;
    }

    public void setTransplantInfos(Map<UUID, TransplantInfo> transplantInfos) {
        this.transplantInfos = transplantInfos;
    }

    protected void readTransformationsInfos(Map<UUID, Transformation> transformations) {
        transformationInfos = TransformationInfo.fromTransformations(transformations.values(), getLoadMessages());
        for (TransformationInfo ti : transformationInfos) {
            for (TransplantInfo tp : ti.getTransplants()) {
                getTransplantInfos().put(tp.getTransformation().getIndex(), tp);
            }
        }
    }

    public Collection<TransformationInfo> getTransformationInfos() {
        return transformationInfos;
    }

    public void setTransformationInfos(Collection<TransformationInfo> infos) {
        transformationInfos = infos;
    }

    @Override
    public void read(Map<UUID, Transformation> transformations) {
        if ( getTransformationInfos().size() == 0 ) {
            readTransformationsInfos(transformations);
        }
    }
}
