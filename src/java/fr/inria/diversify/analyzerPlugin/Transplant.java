package fr.inria.diversify.analyzerPlugin;

import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * Representation of the Transplant containing necessary data for the plugin to work
 *
* Created by marodrig on 15/09/2014.
*/
public class Transplant extends CodePosition {

    private HashMap<String, Float> classificationMap;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public enum Visibility {show, hide, unclassified}

    /**
     * Visibility of the the Transplant in the plugin IDE
     */
    private Visibility visibility = Visibility.show;

    /**
     * Spoon type of the transplant (CtBlock, CtThrowImpl...)
     */
    private String spoonType;

    /**
     * Type of the transplant, for example: Replace, add, delete, replaceSteroids, etc.
     */
    private String type;

    /**
     * Index of the transplant. Kind of an I.D.
     */
    private int index;

    /**
     * A coma separated set of tags to identify the transplant.
     */
    private String tags;

    /**
     * Transformation that this transplant and its TP belongs to
     */
    private Transformation transformation;

    //Indicates if the transformation is applied or not.
    private boolean transformationApplied = false;

    /**
     * Variable map that applies the transformation of the transplant
     */
    private String variableMap;

    /**
     *
     */
    private TransformationRepresentation transplantationPoint;

    /**
     * Stores the last classification weight assigned to this transplant by the last filter operation
     * @param functionName Name of the function assignment weight
     * @param weight Weight assigned by the function
     */
    public void setClassification(String functionName, float weight) {
        if ( classificationMap == null ) {
            classificationMap = new HashMap<String, Float>();
        }
        classificationMap.put(functionName, weight);
    }

    /**
     *  indicates in the classification function passed as parameters has already classified this transplant
     * @param functionName   function to classify the transplant
     * @return true if the function has already classified the transplant
     */
    public boolean isAlreadyClassified(String functionName) {
        return classificationMap != null && classificationMap.containsKey(functionName);
    }

    /**
     * Obtains the classification value assigned to this transplant by the function passed as parameters
     * @param functionName  classification function  name
     */
    public float getClassification(String functionName) {
        if ( classificationMap == null ) {
            classificationMap = new HashMap<String, Float>();
        }
        return classificationMap.containsKey(functionName) ? classificationMap.get(functionName) : 0;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getSpoonType() {
        return spoonType;
    }

    public void setSpoonType(String spoonType) {
        this.spoonType = spoonType;
    }

    public String toString() {
        return type + (getPosition() == null ? "" : "-" + getPosition());
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public boolean isTransformationApplied() {
        return transformationApplied;
    }

    public void setTransformationApplied(boolean value) {
        this.transformationApplied = value;
    }

    public String getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(String variableMap) {
        this.variableMap = variableMap;
    }


    public TransformationRepresentation getTransplantationPoint() {
        return transplantationPoint;
    }

    public void setTransplantationPoint(TransformationRepresentation transplantationPoint) {
        this.transplantationPoint = transplantationPoint;
    }

    /**
     * Indicates if the transplant is applied
     * @return true if applied
     */
    public boolean isApplied() {
        return getTransplantationPoint() != null &&
                getTransplantationPoint().isTransplantApplied(this);
    }
}
