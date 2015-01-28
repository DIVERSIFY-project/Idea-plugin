package fr.inria.diversify.analyzerPlugin.components;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import fr.inria.diversify.analyzerPlugin.model.TransformationInfo;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factories.SpoonMetaFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by marodrig on 27/01/2015.
 */
public class TestEyeProjectComponent extends AbstractProjectComponent {

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
            program = new InputProgram();
            program.setSourceCodeDir(basePath + "\\src\\main\\java");
            program.setFactory(new SpoonMetaFactory().buildNewFactory(program.getSourceCodeDir(), 7));
        } catch (Exception e) {
            throw new IllegalStateException("Not a maven project or maven structure not supported");
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
        if (visibleInfos == null) return this.infos;
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
}
