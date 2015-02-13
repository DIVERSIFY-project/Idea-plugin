package fr.inria.diversify.analyzerPlugin.components;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.*;

/**
 * A class to restore all backup files from the backup storage
 * Created by marodrig on 12/02/2015.
 */
public class RestoreBackupFileVisitor extends SimpleFileVisitor<Path> {

    /**
     * A proxy class to acces the Files.copy, allowing to Unit test the RestoreBackup... class
     */
    public static class VisitorFiles {
        public void copy(Path src, Path dest) throws IOException {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        }

        public void delete(Path path) throws IOException {
            Files.delete(path);
        }

        public void deleteFile(Path file) {
            file.toFile().delete();
        }
    }

    /**
     * Destination base path
     */
    private final Path srcsBasePath;

    /**
     * Source base path
     */
    private final Path bkUpBasePath;

    /**
     * Proxy to the S.O. copy system
     */
    private VisitorFiles filesOps;

    /**
     * A file visitor to restore backups from the backup folder
     *
     * @param backupBasePath Base path to all backup files
     * @param srcBasePath    Base path to the original source files
     */
    public RestoreBackupFileVisitor(String backupBasePath, String srcBasePath) {
        super();
        this.bkUpBasePath = Paths.get(backupBasePath).toAbsolutePath();
        this.srcsBasePath = Paths.get(srcBasePath).toAbsolutePath();
    }

    /**
     * Copies a backup file to a mirror folder in the src file
     *
     * @param file
     * @param attr
     * @return
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isRegularFile() && file.toString().toLowerCase().endsWith(".backup") ) {
            Path bkp = bkUpBasePath.relativize(file);
            Path dest = srcsBasePath.resolve(bkp);
            String s = dest.toString();
            dest = Paths.get(s.substring(0, s.length() - 7));
            try {
                getFilesOps().copy(file, dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        getFilesOps().deleteFile(file);
        return CONTINUE;
    }



    /**
     * Erase the modification dir
     * @param dir
     * @param exc
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        getFilesOps().delete(dir);
        return CONTINUE;
    }

    public VisitorFiles getFilesOps() {
        if (filesOps == null) filesOps = new VisitorFiles();
        return filesOps;
    }

    public void setFilesOps(VisitorFiles filesOps) {
        this.filesOps = filesOps;
    }
}
