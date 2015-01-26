package fr.inria.diversify.analyzerPlugin.model;

import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.diversification.InputConfiguration;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.diversification.accessors.SourceAccesor;
import fr.inria.diversify.diversification.accessors.TypeAccesor;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;

import java.util.HashMap;

/**
 * Representation of the Transplant containing necessary data for the plugin to work
 * <p/>
 * Created by marodrig on 15/09/2014.
 */
public class TransplantInfo extends CodePosition {

    private Boolean containsInnocuousCalls = null;

    private HashMap<String, Float> classificationMap;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Indicates if there are innocuous calls in the transplant. Finding them is extremely expensive so store the data
     *
     * @return
     */
    public Boolean getContainsInnocuousCalls() {
        return containsInnocuousCalls;
    }

    public void setContainsInnocuousCalls(Boolean containsInnocuousCalls) {
        this.containsInnocuousCalls = containsInnocuousCalls;
    }

    public boolean isVisible() {
        return visibility.equals(Visibility.show);
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
    private TransformationInfo transplantationPoint;

    /**
     * Stores the last classification weight assigned to this transplant by the last filter operation
     *
     * @param functionName Name of the function assignment weight
     * @param weight       Weight assigned by the function
     */
    public void setClassification(String functionName, float weight) {
        if (classificationMap == null) {
            classificationMap = new HashMap<String, Float>();
        }
        classificationMap.put(functionName, weight);
    }

    /**
     * indicates in the classification function passed as parameters has already classified this transplant
     *
     * @param functionName function to classify the transplant
     * @return true if the function has already classified the transplant
     */
    public boolean isAlreadyClassified(String functionName) {
        return classificationMap != null && classificationMap.containsKey(functionName);
    }

    /**
     * Obtains the classification value assigned to this transplant by the function passed as parameters
     *
     * @param functionName classification function  name
     */
    public float getClassification(String functionName) {
        if (classificationMap == null) {
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

    /**
     * Initializes the transformation belonging to this transplant representation
     */
    public void initTransformation(InputConfiguration inputConfiguration) {

        //Don't do this again if we already have a transformation
        if ( getTransformation() != null ) return;

        SourceAccesor srcAccessor = new SourceAccesor();
        TypeAccesor typeAccesor = new TypeAccesor();


        InputProgram inputProgram = inputConfiguration.getInputProgram();
        TransformationInfo parentTP = getTransplantationPoint();
        CodeFragment pot = inputProgram.findCodeFragment(parentTP.getPosition(),
                parentTP.getSource(), srcAccessor);
        if ( pot == null ) {
            pot = inputProgram.findCodeFragment(parentTP.getPosition(),
                    parentTP.getSpoonType(), typeAccesor);
        }
        if (pot == null) throw new RuntimeException("Unable to find pot");

        CodeFragment transplant = null;
        if ( !getType().equals("delete") ) {
            transplant = inputProgram.findCodeFragment(getPosition(), getSource(), srcAccessor);
            if (transplant == null) {
                transplant = inputProgram.findCodeFragment(parentTP.getPosition(),
                        getSpoonType(), typeAccesor);
            }
            if (transplant == null) throw new RuntimeException("Unable to find transplant");
        }

        if (getType().equals("delete")) {
            ASTDelete trans = new ASTDelete();
            trans.setTransplantationPoint(pot);
            setTransformation(trans);
        } else if (getType().contains("replace")) {
            if (transplant == null) return;
            ASTReplace trans = new ASTReplace();
            trans.setTransplantationPoint(pot);
            trans.setTransplant(transplant);
            setTransformation(trans);
        } else if (getType().contains("add")) {
            if (transplant == null) return;
            ASTAdd trans = new ASTAdd();
            trans.setTransplantationPoint(pot);
            trans.setTransplant(transplant);
            setTransformation(trans);
        }
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


    public TransformationInfo getTransplantationPoint() {
        return transplantationPoint;
    }

    public void setTransplantationPoint(TransformationInfo transplantationPoint) {
        this.transplantationPoint = transplantationPoint;
    }

    /**
     * Indicates if the transplant is applied
     *
     * @return true if applied
     */
    public boolean isApplied() {
        return getTransplantationPoint() != null &&
                getTransplantationPoint().isTransplantApplied(this);
    }
}
