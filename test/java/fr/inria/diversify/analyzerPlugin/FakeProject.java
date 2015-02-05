package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectImpl;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import fr.inria.diversify.analyzerPlugin.components.TestEyeProjectComponent;
import fr.inria.diversify.ut.MockInputProgram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

import java.io.File;

/**
 * Created by marodrig on 27/01/2015.
 */
public class FakeProject implements Project {

    private static final String BASE_PATH = "test\\data\\test-project";

    private TestEyeProjectComponent testEyeComponent = null;

    private String basePath;

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @Override
    public VirtualFile getBaseDir() {
        return null;
    }

    @Override
    public String getBasePath() {
        if ( basePath == null ) return new File(BASE_PATH).getAbsolutePath();
        return new File(basePath).getAbsolutePath();
    }

    @Nullable
    @Override
    public VirtualFile getProjectFile() {
        return null;
    }

    @NotNull
    @Override
    public String getProjectFilePath() {
        return null;
    }

    @Nullable
    @Override
    public String getPresentableUrl() {
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getWorkspaceFile() {
        return null;
    }

    @NotNull
    @Override
    public String getLocationHash() {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public BaseComponent getComponent(@NotNull String s) {
        return null;
    }

    @Override
    public <T> T getComponent(@NotNull Class<T> aClass) {
        if ( testEyeComponent == null ) {
            testEyeComponent = new TestEyeProjectComponent(this);
            testEyeComponent.setProgram(new MockInputProgram());
        }
        return (T)testEyeComponent;
    }

    @Override
    public <T> T getComponent(@NotNull Class<T> aClass, T t) {
        return null;
    }

    @Override
    public boolean hasComponent(@NotNull Class aClass) {
        return aClass.equals(TestEyeProjectComponent.class);
    }

    /**
     * @param aClass
     * @deprecated
     */
    @NotNull
    @Override
    public <T> T[] getComponents(@NotNull Class<T> aClass) {
        return null;
    }

    @NotNull
    @Override
    public PicoContainer getPicoContainer() {
        return null;
    }

    @NotNull
    @Override
    public MessageBus getMessageBus() {
        return null;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @NotNull
    @Override
    public <T> T[] getExtensions(@NotNull ExtensionPointName<T> extensionPointName) {
        return null;
    }

    @NotNull
    @Override
    public Condition getDisposed() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
