package fr.inria.diversify.analyzerPlugin.model.io;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by marodrig on 26/08/2015.
 */
public class DialogSavePathProvider implements SavePathProvider {

    Path lastPath = Paths.get("");

    @Override
    public Path getPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save latex file");
        chooser.setCurrentDirectory(
                lastPath.getParent() == null ? lastPath.toFile() : lastPath.getParent().toFile());
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().endsWith(".tex") || f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "LaTeX files";
            }
        });
        if (chooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            lastPath = Paths.get(file.getAbsolutePath());
            return lastPath;
        }
        return Paths.get("");
    }
}
