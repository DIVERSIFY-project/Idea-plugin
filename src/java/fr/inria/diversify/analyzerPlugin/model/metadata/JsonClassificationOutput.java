package fr.inria.diversify.analyzerPlugin.model.metadata;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.persistence.json.output.JsonSectionOutput;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * A custom section to save the tags to the JSON file
 * <p/>
 * <p/>
 * Created by marodrig on 17/02/2015.
 */
public class JsonClassificationOutput extends JsonSectionOutput {

    public static final String TAGS = "tags";

    public static final String STRENGTH = "strength";

    private Collection<TransformationInfo> infos;

    public JsonClassificationOutput(Collection<TransformationInfo> infos) {
        this.infos = infos;
    }

    @Override
    public void write(JSONObject outputObject) {
        super.write(outputObject);

        try {
            JSONObject tags = new JSONObject();

            outputObject.put(TAGS, tags);

            for (TransformationInfo info : infos) {
                for (TransplantInfo t : info.getTransplants()) {
                    tags.put(t.getTransformation().getIndex().toString(), t.getTags());
                }
            }

            JSONObject strength = new JSONObject();
            outputObject.put(STRENGTH, strength);
            for (TransformationInfo info : infos) {
                for (TransplantInfo t : info.getTransplants()) {
                    strength.put(t.getTransformation().getIndex().toString(), t.strength());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Collection<TransformationInfo> getInfos() {
        return infos;
    }

    public void setInfos(Collection<TransformationInfo> infos) {
        this.infos = infos;
    }
}
