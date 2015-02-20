package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.persistence.json.input.JsonSectionInput;
import fr.inria.diversify.transformation.Transformation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Reads the coverage information
 * <p/>
 * Created by marodrig on 17/02/2015.
 */
public class JsonClassificationInput extends JsonTestEyeSectionInput {

    public JsonClassificationInput(Collection<TransformationInfo> infos) {
        super(infos);
    }

    @Override
    public void read(HashMap<UUID, Transformation> transformations) {
        super.read(transformations);
        try {
            if ( !getJsonObject().has(JsonClassificationOutput.TAGS) ) return;
            JSONObject tags = getJsonObject().getJSONObject(JsonClassificationOutput.TAGS);
            for (TransplantInfo ti : getTransplantInfos().values()) {
                String id = ti.getIndex().toString();
                if (tags.has(id)) ti.setTags(tags.getString(id));
            }
        } catch (JSONException e) {
            throwError("Unable to read tags", e, false);
        }
    }

}
