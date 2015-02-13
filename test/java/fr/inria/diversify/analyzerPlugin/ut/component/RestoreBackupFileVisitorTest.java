package fr.inria.diversify.analyzerPlugin.ut.component;
import fr.inria.diversify.analyzerPlugin.components.RestoreBackupFileVisitor;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marodrig on 12/02/2015.
 */
public class RestoreBackupFileVisitorTest {

    public class MockCopier extends RestoreBackupFileVisitor.VisitorFiles {

        @Override
        public void deleteFile(Path p) {

        }

        @Override
        public void delete(Path p) {
            assertTrue(p.toFile().exists());
            List<String> s = Arrays.asList(new String[]{
                    Paths.get("test/data/restoreFilePaths/backup/dir1").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/backup/dir2").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/backup").toAbsolutePath().toString(),
            });
            assertTrue(s.contains(p.toString()));
        }

        @Override
        public void copy(Path src, Path dest) {

            assertTrue(src.toFile().exists());
            assertTrue(dest.toFile().exists());

            List<String> s = Arrays.asList(new String[]{
                    Paths.get("test/data/restoreFilePaths/backup/dir1/file2.txt.backup").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/backup/dir1/file3.txt.backup").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/backup/file1.txt.backup").toAbsolutePath().toString(),
            });
            List<String> d = Arrays.asList(new String[]{
                    Paths.get("test/data/restoreFilePaths/src/main/dir1/file2.txt").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/src/main/dir1/file3.txt").toAbsolutePath().toString(),
                    Paths.get("test/data/restoreFilePaths/src/main/file1.txt").toAbsolutePath().toString(),
            });
            assertTrue(s.contains(src.toString()));
            assertTrue(d.contains(dest.toString()));
            assertEquals(src.getFileName().toString(), dest.getFileName().toString() + ".backup");
        }
    }

    @Test
    public void testRestore() throws IOException {
        RestoreBackupFileVisitor v = new RestoreBackupFileVisitor(
                "test/data/restoreFilePaths/backup", "test/data/restoreFilePaths/src/main");
        v.setFilesOps(new MockCopier());
        Files.walkFileTree(Paths.get("test/data/restoreFilePaths/backup").toAbsolutePath(), v);
    }
}
