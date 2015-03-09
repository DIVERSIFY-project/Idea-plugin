package fr.inria.diversify.analyzerPlugin.model.io;

import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClassifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Collection;

/**
 * Created by marodrig on 22/10/2014.
 */
public class PluginDataExport {

    private PluginDataLoader pluginDataLoader;

    /**
     * Current representations obtained from the JSON  file
     */
    private Collection<TransformationInfo> representations;


    private Collection<TransformClassifier> clasifiers;

    /**
     * Current representations obtained from the JSON  file
     */
    public Collection<TransformationInfo> getRepresentations() {
        return representations;
    }

    public void setRepresentations(Collection<TransformationInfo> representations) {
        this.representations = representations;
    }

    /**
     * all function classifiers
     */
    public Collection<TransformClassifier> getClasifiers() {
        return clasifiers;
    }

    public void setClasifiers(Collection<TransformClassifier> clasifiers) {
        this.clasifiers = clasifiers;
    }

    /* Original file containing the transformations. The contain off the file will be passed
     * to the output "as is" in order to  maintain backward compatibility with older software
     */
    private String originalJSONFIle;

    /**
     * Stores all the information collected during the use of the  plug-in  in an extended version of the input formats
     * The store file can still be read by the plugin  as well as another application using this  format
     *
     * @param fileName name of the output file.
     */
    public void save(String fileName) throws IOException, JSONException {


        BufferedReader r = new BufferedReader(new FileReader(getOriginalJSONFIle()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        JSONObject parentObject;
        JSONArray sourceJSONArray;
        try {
            sourceJSONArray = new JSONArray(sb.toString());
            parentObject = new JSONObject();
            parentObject.put("transformations", sourceJSONArray);
        } catch (JSONException e) {
            parentObject = new JSONObject(sb.toString());
        }

        //Collect tag for all representations
        JSONObject tags = new JSONObject();
        //Put tags
        for (TransformationInfo rep : representations) {
            for (TransplantInfo t : rep.getTransplants()) {
                if (!(t.getTags() == null || t.getTags().isEmpty())) {
                    tags.put(String.valueOf(t.getIndex()), t.getTags());
                }
            }
        }
        // save them
        parentObject.put("tags", tags);

        //Collect classifications functions
        JSONObject classMap = new JSONObject();

        for (TransformClassifier c : clasifiers) {
            if (!c.isUserFilter()) {
                JSONObject cjson = new JSONObject();
                cjson.put("index", c.getClass().getSimpleName());
                cjson.put("weight", c.getWeight());
                classMap.put(c.getDescription(), cjson);
            }
        }
        parentObject.put("classifications", classMap);

        //save them to file
        JSONArray repArray = new JSONArray();
        for (TransformationInfo rep : representations) {
            for (TransplantInfo t : rep.getTransplants()) {
                for (TransformClassifier c : clasifiers) {
                    float v = t.isAlreadyClassified(c.getDescription()) ? t.getClassification(c.getDescription()) : 0;
                    if ( v != 0 && !c.isUserFilter() ) {
                        repArray.put(t.getIndex());
                        repArray.put(classMap.getJSONObject(c.getDescription()).getString("index"));
                        repArray.put(t.getType());
                    }
                }
            }
        }
        parentObject.put("transformationClass", repArray);

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        parentObject.write(bw);
        bw.close();

    }

    public String getOriginalJSONFIle() {
        return originalJSONFIle;
    }

    public void setOriginalJSONFIle(String originalJSONFIle) {
        this.originalJSONFIle = originalJSONFIle;
    }
}
