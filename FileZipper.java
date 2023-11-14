package ClassyCrafT.gui.swing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Luka Savic
 * Please see {@link }
 *
 * [java.util]
 * [java.io]
 *
 */
public class FileZipper {
    private final String extension = ".zip";

    public FileZipper() {
    }

    public void Zip(File file) {
        String path = file.getAbsolutePath();

        try (FileOutputStream fos = new FileOutputStream(path + extension); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolder(path, path, zos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void UnZip(File file) {
        String destDir = getNameWithoutExtension(file.getAbsolutePath());
        File destFolder = new File(destDir);
        if (destFolder.exists()) {
            destFolder.delete();
        }
        destFolder.mkdir();

        unZip(file, destDir);
    }

    private void zipFolder(String folderPath, String sourceFolder, ZipOutputStream zos) throws IOException {
        File folder = new File(folderPath);
        if (!folder.isDirectory())
            return;

        for (String fileName : folder.list()) {
            String filePath = folderPath + File.separator + fileName;
            File test = new File(filePath);

            if (test.isDirectory()) {
                String zipEntryName = filePath.replace(sourceFolder + File.separator, "");
                ZipEntry zipEntry = new ZipEntry(zipEntryName + "/");
                zos.putNextEntry(zipEntry);
                zos.closeEntry();
                zipFolder(filePath, sourceFolder, zos);
                continue;
            }

            FileInputStream fis = new FileInputStream(filePath);
            String zipEntryName = filePath.replace(sourceFolder + File.separator, "");
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            fis.close();
        }
    }

    private void unZip(File file, String destDir) {
        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath()); ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destDir + File.separator + fileName);
                File checkFolders = new File(newFile.getAbsolutePath().replace("\\" + newFile.getName(), ""));
                if (!checkFolders.exists()) {
                    checkFolders.mkdirs();
                }

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                    zis.closeEntry();
                    zipEntry = zis.getNextEntry();
                    continue;
                }

                try (FileOutputStream fos = new FileOutputStream(newFile);) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = zis.read(bytes)) >= 0) {
                        fos.write(bytes, 0, length);
                    }
                }

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(File file) {
        int lastIndexOfDot = file.getName().lastIndexOf(".");
        if (lastIndexOfDot == -1) return "";
        return file.getName().substring(lastIndexOfDot);
    }

    private String getNameWithoutExtension(String path) {
        int lastIndexOfDot = path.lastIndexOf(".");
        return path.substring(0, lastIndexOfDot);
    }
}
