package fr.inria.diversify.analyzerPlugin;

import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by marodrig on 27/01/2015.
 */
public class MyFakeVirtualFile extends StubVirtualFile {

    public static final String ABSOLUTE_PATH = "uzr/h0m3/pr0j3cts";

    private final String path;

    public MyFakeVirtualFile() {
        path = ABSOLUTE_PATH;
    }

    //public MyFakeVirtualFile(String path) { this.path = path; }

    @NotNull
    public String getCanonicalPath() {
        return ABSOLUTE_PATH;
    }
}
