package fr.inria.diversify.analyzerPlugin.components;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.analyzerPlugin.model.TransplantInfo;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.ClassifierFactory;
import fr.inria.diversify.analyzerPlugin.model.clasifiers.TransformClasifier;
import fr.inria.diversify.analyzerPlugin.model.orders.AlphabeticallOrder;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import fr.inria.diversify.persistence.PersistenceException;
import fr.inria.diversify.persistence.json.input.JsonSosiesInput;
import fr.inria.diversify.transformation.Transformation;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeProjectComponent extends AbstractProjectComponent {

    public static String TEMP_MOD = "/.modBackup";

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

    /**
     * Order to sort transformations
     */
    private Comparator<TransformationInfo> order;

    /**
     * Errors registered so far
     */
    private List<String> logMessages;


    public TestEyeProjectComponent(Project project) {
        super(project);
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

    @Override
    public void projectOpened() {
        restoreBackUp();
    }

    @Override
    public void projectClosed() {
        restoreBackUp();
    }

    /**
     * The apply transplant saves backups of the original source so they can be restored.
     * This method restores such back ups.
     * <p/>
     * Done listening to: https://soundcloud.com/mixpak/machel-montano-go-down
     * * Wine it!*
     */
    private void restoreBackUp() {
        try {
            String s = myProject.getBasePath();
            File f = new File(s + TEMP_MOD);
            if (f.exists()) {
                Files.walkFileTree(f.toPath(), new RestoreBackupFileVisitor(s + TEMP_MOD, getProgram().getSourceCodeDir()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
     * Get the list of all classifiers
     *
     * @return
     */
    public List<TransformClasifier> getClassifiers() {
        if (classifiers == null) {
            classifiers = getClassifierFactory().buildClassifiers();
            visibleClassifiers = new HashMap<>();
            for (TransformClasifier t : classifiers) {
                visibleClassifiers.put(t.getClass(), true);
            }
        }

        return classifiers;
    }

    /**
     * Get the list visible classifiers organized by class and
     *
     * @return
     */
    protected HashMap<Class<? extends TransformClasifier>, Boolean> getVisibleClassifiers() {
        if (visibleClassifiers == null) getClassifiers(); //Init visible classifiers
        return visibleClassifiers;
    }

    public void setClassifierFactory(ClassifierFactory factory) {
        classifierFactory = factory;
        //Build classifiers using this factory
        getClassifiers();
    }

    public ClassifierFactory getClassifierFactory() {
        if (classifierFactory == null) classifierFactory = new ClassifierFactory();
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

        return getVisibleClassifiers().get(aClass);
    }

    /**
     * Set the visibility that the filter will award
     *
     * @param aClass
     * @param value
     */
    public void setVisibleClassifiers(Class<? extends TransformClasifier> aClass, boolean value) {
        if (aClass == null) unclassifiedVisibility = value;
        else getVisibleClassifiers().put(aClass, value);
    }

    public void setShowClassifiersIntersection(boolean value) {
        this.showClassifiersIntersection = value;
    }

    /**
     * Sets all classifications visibility
     */
    public void setAllClassificationsVisibility(boolean value) {
        for (Class<? extends TransformClasifier> k : getVisibleClassifiers().keySet()) {
            setVisibleClassifiers(k, value);
        }
    }

    public void switchViewIntersection() {
        setShowClassifiersIntersection(!showClassifiersIntersection);
    }

    /**
     * Filter and sort all infos
     *
     * @param progressIndicator
     */
    public void filterAndSort(ProgressIndicator progressIndicator) {
        filter(progressIndicator);
        sort();
    }


    /**
     * Filter the transformations by the visible classifiers
     *
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
     */
    public void sort() {
        getVisibleInfos().sort(getOrder());
    }

    public void setOrder(Comparator<TransformationInfo> order) {
        this.order = order;
    }

    public Comparator<TransformationInfo> getOrder() {
        if (order == null) order = new AlphabeticallOrder();
        return order;
    }

    /**
     * Hide all transformations
     *
     * @param progressIndicator
     */
    public void hideAll(ProgressIndicator progressIndicator) {
        setAllClassificationsVisibility(false);
        setShowClassifiersIntersection(true);
        filterAndSort(progressIndicator);
    }

    /**
     * Shows all transformations
     *
     * @param progressIndicator
     */
    public void showAll(ProgressIndicator progressIndicator) {
        setAllClassificationsVisibility(true);
        setShowClassifiersIntersection(false);
        filterAndSort(progressIndicator);
    }

    public boolean getShowClassifiersIntersection() {
        return showClassifiersIntersection;
    }


    /**
     * Loads the infos from a file
     *
     * @param filePath Path of the file containing the transformations
     * @throws FileNotFoundException
     */
    public void loadInfos(String filePath) throws FileNotFoundException {
        loadInfos(new InputStreamReader(new FileInputStream(filePath)));
    }

    /**
     * Loads the infos from a JSON residing in the reader
     *
     * @param reader
     */
    public void loadInfos(InputStreamReader reader) {
        JsonSosiesInput input = new JsonSosiesInput(reader, getProgram());
        getLogMessages().clear();
        try {
            Collection<Transformation> ts = input.read();
            setInfos(new ArrayList<>(TransformationInfo.fromTransformations(ts, input.getLoadMessages())));
        } catch (PersistenceException e) {
            //Save all errors and warnings
            getLogMessages().addAll(input.getLoadMessages());
            throw e;
        }
        getLogMessages().addAll(input.getLoadMessages());
    }

    public void setLogMessages(List<String> logMessages) {
        this.logMessages = logMessages;
    }

    public List<String> getLogMessages() {
        if (logMessages == null) logMessages = new ArrayList<>();
        return logMessages;
    }
}
