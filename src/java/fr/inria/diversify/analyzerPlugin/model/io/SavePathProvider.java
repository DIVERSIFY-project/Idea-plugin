package fr.inria.diversify.analyzerPlugin.model.io;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Created by marodrig on 26/08/2015.
 */
public interface SavePathProvider {

    public Path getPath();

}
