package fr.inria.diversify.analyzerPlugin.ut;

import fr.inria.diversify.analyzerPlugin.model.io.SavePathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by marodrig on 26/08/2015.
 */
public class FakeSavePathProvider implements SavePathProvider {

    public static String FILE_NAME = "filename.txt";

    @Override
    public Path getPath() {
        return Paths.get(FILE_NAME);
    }
}
