package fr.inria.diversify.analyzerPlugin.components;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import org.jetbrains.annotations.NotNull;
import org.kevoree.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeProjectComponent extends AbstractProjectComponent {

    /**
     * Constant to assign to the "unclassified" category when filtering
     */
    public static final Class<? extends TransformClasifier> UNCLASSIFIED = null;

    /**
     * All classifiers
     */
    private List<TransformClasifier> classifiers;

    /**
     * All infos in the project
     */
    private List<TransformationInfo> infos;

    /**
     * All visible infos in the project
     */
    private List<TransformationInfo> visibleInfos;

    /**
     * Program that represents the project
     */
    private InputProgram program;

    /**
     * A dictionary with classifier visibility indexed by class
     */
    private HashMap<Class<? extends TransformClasifier>, Boolean> visibleClassifiers;

    /**
     * Indicates if the intersection of classifiers must be shown if one of them hides the classification
     */
    private boolean showClassifiersIntersection;

    /**
     * Visibility of non classified transformations
     */
    private boolean unclassifiedVisibility = true;

    /**
     * The classifier factory
     */
    private ClassifierFactory classifierFactory;


    public TestEyeProjectComponent(Project project) {
        super(project);
        visibleClassifiers = new HashMap<>();
    }

    /**
     * Inits the program given the base path of the project
     *
     * @param basePath base path of the project
     * @throws java.lang.RuntimeException that must be handled by
     */
    private void initProgram(String basePath) {
        try {
            MavenDependencyResolver dr = new MavenDependencyResolver();
            dr.DependencyResolver(basePath + "\\pom.xml");
        } catch (Exception e) {
            throw new IllegalStateException("Not a maven project, or errors during parsing or maven structure not supported");
        }
        try {
            program = new InputProgram();
            program.setProgramDir(basePath);
            program.setSourceCodeDir(basePath + "\\src\\main\\java");
            program.setFactory(new SpoonMetaFactory().buildNewFactory(program.getSourceCodeDir(), 7));
        } catch (Exception e) {
            throw new IllegalStateException("Error while indexing program");
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TestEye.transformations";
    }

    /**
     * Set the list of all the Transformation infos attached to this project
     *
     * @param infos
     */
    public void setInfos(List<TransformationInfo> infos) {
        this.infos = infos;
    }

    /**
     * Set the list of all visible (in the UI) Transformation infos attached to this project
     *
     * @param infos
     */
    public void setVisibleInfos(List<TransformationInfo> infos) {

        this.visibleInfos = infos;
    }

    /**
     * Get the list of all visible (in the UI) Transformation infos attached to this project
     */
    public List<TransformationInfo> getInfos() {
        return infos;
    }

    /**
     * Get the list of all visible Transformation infos attached to this project
     */
    public List<TransformationInfo> getVisibleInfos() {
        if (visibleInfos == null) visibleInfos = new ArrayList<>(this.infos);
        return visibleInfos;
    }

    /**
     * Returns the input program representing the current project
     *
     * @return
     */
    public InputProgram getProgram() {
        if (program == null) initProgram(myProject.getBasePath());
        return program;
    }

    public void setProgram(InputProgram program) {
        this.program = program;
    }

    /**
     * Switch on/off the classifier
     *
     * @param classifierClass Class of the classifier to switch
     */
    public void switchClassifier(Class<? extends TransformClasifier> classifierClass) {
        visibleClassifiers.put(classifierClass, !visibleClassifiers.get(classifierClass));
    }

    /**
     * Get the list of classifiers
     *
     * @return
     */
    public List<TransformClasifier> getClassifiers() {
        if (classifiers == null) {
            classifiers = getClassifierFactory().buildClassifiers();
            for (TransformClasifier t : classifiers) {
                visibleClassifiers.put(t.getClass(), true);
            }
        }

        return classifiers;
    }

    public void setClassifierFactory(ClassifierFactory factory) {
        classifierFactory = factory;
        //Build classifiers using this factory
        getClassifiers();
    }

    public ClassifierFactory getClassifierFactory() {
        if ( classifierFactory == null ) classifierFactory = new ClassifierFactory();
        return classifierFactory;
    }

    /**
     * Indicate if the filter hides or shows
     *
     * @param aClass
     * @return
     */
    public boolean isFilterVisible(Class<? extends TransformClasifier> aClass) {

        if (aClass == null) return unclassifiedVisibility;

        return visibleClassifiers.get(aClass);
    }

    /**
     * Set the visibility that the filter will award
     *
     * @param aClass
     * @param value
     */
    public void setVisibleClassifiers(Class<? extends TransformClasifier> aClass, boolean value) {
        if  ( aClass == null ) unclassifiedVisibility = value;
        else visibleClassifiers.put(aClass, value);
    }

    public void setShowClassifiersIntersection(boolean value) {
        this.showClassifiersIntersection = value;
    }

    /**
     * Sets all classifications visibility
     */
    public void setAllClassificationsVisibility(boolean value) {
        for (Class<? extends TransformClasifier> k : visibleClassifiers.keySet()) {
            visibleClassifiers.put(k, value);
        }
    }

    public void switchViewIntersection() {
        setShowClassifiersIntersection(!showClassifiersIntersection);
    }

    /**
     * Filter and sort all infos
     * @param progressIndicator
     */
    public void filterAndSort(ProgressIndicator progressIndicator) {
        filter(progressIndicator);
        sort(progressIndicator);
    }


    /**
     * Filter the transformations by the visible classifiers
     * @param progressIndicator
     */
    private void filter(ProgressIndicator progressIndicator) {
        int i = 0;

        if (getVisibleInfos() == null)
            setVisibleInfos(new ArrayList<TransformationInfo>());
        else getVisibleInfos().clear();

        int progress = 1;
        for (TransformationInfo info : getInfos()) {

            if (progressIndicator.isCanceled()) return;
            progressIndicator.setFraction((double) progress / (double) getInfos().size());
            progress++;


            for (TransplantInfo transplant : info.getTransplants()) {

                transplant.setVisibility(TransplantInfo.Visibility.unclassified);
                for (TransformClasifier c : getClassifiers()) {
                    float v;
                    //the only way classification functions modify the score assigned
                    //is by user input, therefore only user filters must be reclassified each time
                    if (!c.isUserFilter() && transplant.isAlreadyClassified(c.getDescription())) {
                        //retrieve classification already assignment to the transformation
                        v = transplant.getClassification(c.getDescription());
                    } else {
                        // evaluates the transformation
                        v = c.value(transplant);
                        transplant.setClassification(c.getDescription(), v);
                    }

                    //sets the visibility on/off depending on the show intersection option
                    if (v != 0) {
                        if (isFilterVisible(c.getClass())) {
                            transplant.setVisibility(TransplantInfo.Visibility.show);
                            if (isFilterVisible(UNCLASSIFIED)) break;
                        } else {
                            transplant.setVisibility(TransplantInfo.Visibility.hide);
                            if (!isFilterVisible(UNCLASSIFIED)) break;
                        }
                    }
                }
                //If no classification functions and was able to classify the transplant
                //then the transplant become  unclassified and its visibility is assignment depending
                //on a special case of classification function
                if (transplant.getVisibility() == TransplantInfo.Visibility.unclassified) {
                    TransplantInfo.Visibility vis = isFilterVisible(UNCLASSIFIED) ?
                            TransplantInfo.Visibility.show : TransplantInfo.Visibility.hide;
                    transplant.setVisibility(vis);
                }


            }

            if (info.hasVisibleTransplants()) getVisibleInfos().add(info);
        }
    }

    /**
     * Sort infos using the given comparator
     * @param progressIndicator
     */
    public void sort(ProgressIndicator progressIndicator) {

    }
}
