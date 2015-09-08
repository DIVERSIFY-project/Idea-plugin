package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by marodrig on 03/02/2015.
 */
public class TestEyeIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, TestEyeIcons.class);
    }
    public static final Icon AddTransformation = load("/icons/add.png"); // 16x16
    public static final Icon DeleteTransformation = load("/icons/delete.png"); // 16x16
    public static final Icon ReplaceTransformation = load("/icons/replace.png"); // 16x16
    public static final Icon TransplantPoint = load("/icons/tp.png"); // 16x16
    public static final Icon Warning = load("/icons/Warning.png"); // 16x16
    public static final Icon Open = load("/icons/menu-open.png"); // 16x16
    public static final Icon OpenLog = load("/icons/testSourceFolder.png"); // 16x16
    public static final Icon Test = load("/icons/test.png"); // 16x16
    public static final Icon Assert = load("/icons/assert.png"); // 16x16
    public static final Icon Save = load("/icons/save.png"); // 16x16
    public static final Icon Sort = load("/icons/sort.png"); // 16x16
    public static final Icon Latex = load("/icons/tex.png"); // 16x16
}
