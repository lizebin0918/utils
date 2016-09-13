package com.yaodian.utils.zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * ZIP工具
 * required jdk1.7+
 * dependence:
 *    <dependency>
 *    <groupId>org.apache.ant</groupId>
 *    <artifactId>ant</artifactId>
 *    <version>1.9.7</version>
 *    </dependency>
 * @author 李泽彬
 * @since 1.0
 */
public abstract class ZipUtils {

    private static final String CHARSET = "utf-8";
    public static final int BUFFER = 2048;
    public static final String EXT = ".gz";

    /**
     * 数据压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compressGZIP(byte[] data) throws Exception {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            compressFileGZIP(bais, baos);
            byte[] output = baos.toByteArray();
            baos.flush();
            return output;
        }
    }

    /**
     * 文件压缩
     *
     * @param file
     * @throws Exception
     */
    public static void compressFileGZIP(File file) throws Exception {
        compressFileGZIP(file, true);
    }

    /**
     * 文件压缩
     *
     * @param file
     * @param delete
     *            是否删除原始文件
     * @throws Exception
     */
    public static void compressFileGZIP(File file, boolean delete) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);) {
            compressFileGZIP(fis, fos);
            fos.flush();
            if (delete) {
                file.deleteOnExit();
            }
        }
    }

    /**
     * 数据压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    private static void compressFileGZIP(InputStream is, OutputStream os)
            throws IOException {
        try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
        }
    }

    /**
     * 文件压缩
     *
     * @param path
     * @throws Exception
     */
    public static void compressFileGZIP(String path) throws Exception {
        compressFileGZIP(path, true);
    }

    /**
     * 文件压缩
     *
     * @param path
     * @param delete
     *            是否删除原始文件
     * @throws Exception
     */
    public static void compressFileGZIP(String path, boolean delete) throws Exception {
        File file = new File(path);
        compressFileGZIP(file, delete);
    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompressGZIP(byte[] data) throws Exception {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            decompressGZIP(bais, baos);
            data = baos.toByteArray();
            baos.flush();
            return data;
        }
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @throws Exception
     */
    public static void decompressFileGZIP(File file) throws Exception {
        decompressFileGZIP(file, true);
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @param delete
     *            是否删除原始文件
     * @throws Exception
     */
    public static void decompressFileGZIP(File file, boolean delete) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(file.getPath().replace(EXT, ""));) {
            decompressGZIP(fis, fos);
            fos.flush();
            if (delete) {
                file.deleteOnExit();
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    private static void decompressGZIP(InputStream is, OutputStream os)
            throws Exception {
        try (GZIPInputStream gis = new GZIPInputStream(is)) {
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
        }
    }

    /**
     * 文件解压缩
     *
     * @param path
     * @throws Exception
     */
    public static void decompressFileGZIP(String path) throws Exception {
        decompressFileGZIP(path, true);
    }

    /**
     * 文件解压缩
     *
     * @param path
     * @param delete
     *            是否删除原始文件
     * @throws Exception
     */
    public static void decompressFileGZIP(String path, boolean delete) throws Exception {
        File file = new File(path);
        decompressFileGZIP(file, delete);
    }


    /**
     * 压缩目录<br/>
     * Created on : 2016-09-13 20:32
     * @author lizebin
     * @version V1.0.0
     * @param dirName 被压缩目录
     * @param zipFilePath 压缩后的文件名称
     * @return
     */
    public static void compressDirectory(String dirName, String zipFilePath) throws IOException {
        File dir = new File(dirName);
        if(dir.isFile()) {
            throw new RuntimeException("文件类型有误");
        }
        List<File> fileList = getSubFiles(dir);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            ZipEntry ze = null;
            byte[] buf = new byte[BUFFER];
            int readLen = 0;
            for (int i = 0; i < fileList.size(); i++) {
                File f = (File) fileList.get(i);
                ze = new ZipEntry(getAbsFileName(dirName, f));
                ze.setSize(f.length());
                ze.setTime(f.lastModified());
                zos.putNextEntry(ze);
                try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
                    while ((readLen = is.read(buf, 0, BUFFER)) != -1) {
                        zos.write(buf, 0, readLen);
                    }
                }
            }
            zos.setEncoding(CHARSET);
        }
    }

    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if (real == null)
                break;
            if (real.equals(base))
                break;
            else
                ret = real.getName() + File.separator + ret;
        }
        return ret;
    }

    private static List getSubFiles(File baseDir) {
        List ret = new ArrayList();
        File[] tmp = baseDir.listFiles();
        for (int i = 0,length=tmp.length; i < length; i++) {
            if (tmp[i].isFile()) {
                ret.add(tmp[i]);
            } else {//目录
                ret.addAll(getSubFiles(tmp[i]));
            }
        }
        return ret;
    }

    /**
     * 解压缩目录<br/>
     * Created on : 2016-09-13 20:28
     * @author lizebin
     * @version V1.0.0
     * @param targetDir 解析到 targetDir 目录下
     * @param zipFilePath 压缩文件路径
     * @param delete 是否删除文件
     * @return
     */
    public static void decompressFileZip(String targetDir, String zipFilePath, boolean delete) throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<?> enu = zipFile.getEntries();

        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();

            String name = zipEntry.getName();

            File file = new File(targetDir + name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            try (
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);) {
                byte[] bytes = new byte[BUFFER];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
            }
        }
        zipFile.close();
        if(delete) {
            Files.deleteIfExists(Paths.get(zipFilePath));
        }
    }

    /*public static void main(String[] args) throws Exception {
        *//*String fileName = "backup";
        compressDirectory("/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cms/upload/" + fileName,
                          "/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cms/upload/123.zip");*//*
        ZipUtils.decompressFileZip("/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cms/upload/",
                                   "/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cms/upload/123.zip", true);
    }*/

}