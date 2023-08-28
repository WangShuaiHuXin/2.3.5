package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.constant.SymbolConstants;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩/解压缩工具类
 * 实现对目标路径及其子路径下的所有文件及空目录的压缩
 * @author Vastfy
 * @date 2022/07/10 09:48
 * @since 1.0.0
 */
@Slf4j
public class ZipFileUtils {

    public static final String FILE_TYPE_ZIP = ".zip";
    private static final String FILE_TYPE_JPG = ".jpg";
    private static final String FILE_TYPE_JPEG = ".jpeg";
    private static final String FILE_TYPE_PNG = ".png";
    private static final String JAVA_TMP_IO_DIR = System.getProperty("java.io.tmpdir");

    /**
     * 缓冲器大小
     */
    private static final int BUFFER = 512;

    private static final Set<String> FILE_TYPE_IMAGE_SET = new HashSet<>(3);

    static {
        FILE_TYPE_IMAGE_SET.addAll(Arrays.asList(FILE_TYPE_JPG, FILE_TYPE_JPEG, FILE_TYPE_PNG));
    }

    /**
     * 判断给定文件是否是zip文件
     * @param fileName 文件名
     * @return zip file if true
     */
    public static boolean isZipFile(String fileName) {
        return StringUtils.hasText(fileName) && fileName.endsWith(ZipFileUtils.FILE_TYPE_ZIP);
    }

    /**
     * 解压zip文件并获取excel文件<br/>
     * 注意：该方式解压后的文件不会自动清理，请调用者在必要时手动清理解压资源，防止过度占用磁盘资源
     * @param inputStream 输入流
     * @param destDir   解压目录，不指定默认为用户临时目录
     * @return  Excel文件
     * @throws IOException 解压失败时
     */
    public static Optional<Resource> extractZipAndGetResource(InputStream inputStream, String destDir, Predicate<File> predicate) throws IOException {
        return extractZipAndGetResource(inputStream, destDir, false, predicate);
    }

    /**
     * 解压zip文件到系统临时目录，并返回解压后的excel文件资源<br/>
     * 注意：该方式解压后的文件不会自动清理，请调用者在必要时手动清理解压资源，防止过度占用磁盘资源
     * @param inputStream 输入流
     * @param autoClean   true：如果excel文件不存在，会自动清理解压后目录
     * @return  Excel文件
     * @throws IOException 解压失败时
     */
    public static Optional<Resource> extractZipAndGetResource(InputStream inputStream, boolean autoClean, Predicate<File> predicate) throws IOException {
        String tempDestDir = JAVA_TMP_IO_DIR + UUID.randomUUID() + File.separator;
        return extractZipAndGetResource(inputStream, tempDestDir, autoClean, predicate);
    }

    /**
     * 解压zip文件到系统临时目录，并返回解压后的excel文件资源<br/>
     * 注意：该方式解压后的文件不会自动清理，请调用者在必要时手动清理解压资源，防止过度占用磁盘资源
     * @param inputStream 输入流
     * @param destDir 解压目录
     * @param autoClean   true：如果excel文件不存在，会自动清理解压后目录
     * @return  Excel文件
     * @throws IOException 解压失败时
     */
    public static Optional<Resource> extractZipAndGetResource(InputStream inputStream, String destDir, boolean autoClean, Predicate<File> predicate) throws IOException {
        if(!StringUtils.hasText(destDir)){
            throw new IllegalArgumentException("Unzip directory not specified");
        }
        if(Objects.isNull(inputStream)){
            throw new IllegalArgumentException("Input stream is null");
        }
        unzip(inputStream, destDir);
        List<File> extractedFiles = getAllFiles(destDir);
        if(!CollectionUtils.isEmpty(extractedFiles)){
            Optional<Resource> excelResource = extractedFiles.stream()
                    .filter(predicate)
                    .findFirst()
                    .map(FileSystemResource::new);
            if(autoClean && !excelResource.isPresent()){
                FileUtils.deleteFinally(extractedFiles);
            }
            return excelResource;
        }
        return Optional.empty();
    }

    /**
     * 解压zip文件到系统临时目录，并返回解压后的指定文件资源<br/>
     *
     * @param inputStream 输入流
     * @param destDir 解压目录
     * @param autoClean   true：如果excel文件不存在，会自动清理解压后目录
     * @return  Excel文件
     * @throws IOException 解压失败时
     */
    public static List<Resource> extractZipAndGetAllResource(InputStream inputStream, String destDir, boolean autoClean, Predicate<File> predicate) throws IOException {
        if(!StringUtils.hasText(destDir)){
            throw new IllegalArgumentException("Unzip directory not specified");
        }
        if(Objects.isNull(inputStream)){
            throw new IllegalArgumentException("Input stream is null");
        }
        unzip(inputStream, destDir);
        List<File> extractedFiles = getAllFiles(destDir);
        if(!CollectionUtils.isEmpty(extractedFiles)){
            List<Resource> excelResource = extractedFiles.stream()
                    .filter(predicate)
                    .map(FileSystemResource::new)
                    .collect(Collectors.toList());
            if(autoClean && !CollectionUtils.isEmpty(excelResource)){
                FileUtils.deleteFinally(extractedFiles);
            }
            return excelResource;
        }
        return new ArrayList<>();
    }

    /**
     * 解压缩方法
     * @param inputStream 压缩文件流
     * @return 是否解压成功
     */
    public static List<File> extractFiles(InputStream inputStream) throws IOException {
        String tempZipFilePath = JAVA_TMP_IO_DIR + UUID.randomUUID() + File.separator;
        unzip(inputStream, tempZipFilePath);
        return getAllFiles(tempZipFilePath);
    }

    private static void unzip(InputStream zipFileInputStream, String destDir) throws IOException {
        log.info("******************解压开始********************");
        log.info("文件将解压至临时目录：{}", destDir);
        long start = System.currentTimeMillis();
        try (ZipArchiveInputStream inputStream = getZipArchiveInputStream(zipFileInputStream)) {
            int counts = 0;
            Path destPath = Paths.get(destDir);
            if (!destPath.toFile().exists()) {
                Files.createDirectories(destPath);
            }
            ZipArchiveEntry entry;
            while ((entry = inputStream.getNextZipEntry()) != null) {
                String entryName = entry.getName();
                String entryFilename = destPath + SymbolConstants.SLASH_LEFT + entryName;
                Path entryPath = Paths.get(entryFilename);

                // 某些场景下，会出现目录无法创建问题
                Path parent = entryPath.getParent();
                if(!parent.toFile().exists()){
                    Files.createDirectory(parent);
                }

                if (entry.isDirectory()) {
                    if(!entryPath.toFile().exists()){
                        Files.createDirectory(entryPath);
                    }
                    continue;
                }

                if(!entryPath.toFile().exists()){
                    Files.createFile(entryPath);
                }

                try(OutputStream os = new BufferedOutputStream(Files.newOutputStream(entryPath))) {
                    log.info("解压文件的当前路径为：{}", destDir + entry.getName());
                    IOUtils.copy(inputStream, os);
                    counts ++;
                }
            }
            log.info("【解压文件统计】 ==> 总文件：{}个, 总耗时：{}ms.", counts, System.currentTimeMillis() - start);
            log.info("******************解压完毕********************");

        } catch (IOException e) {
            log.error("[unzip] 解压zip文件出错", e);
            throw e;
        }
    }

    private static ZipArchiveInputStream getZipArchiveInputStream(InputStream inputStream) {
        return new ZipArchiveInputStream(new BufferedInputStream(inputStream));
//        return new ZipArchiveInputStream(new BufferedInputStream(inputStream), "GBK");
    }

    private static ZipArchiveOutputStream getZipArchiveOutputStream(File zipFile) throws IOException {
        return new ZipArchiveOutputStream(zipFile);
    }

    public static List<File> getAllFiles(String destPath) {
        File dir = Paths.get(destPath).toFile();
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        List<File> allFiles = new ArrayList<>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (Objects.nonNull(files) && files.length > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        allFiles.add(file);
                    } else {
                        allFiles.addAll(getAllFiles(file.getPath()));
                    }
                }
            }
        } else {
            allFiles.add(dir);
        }
        return allFiles;
    }

    /**
     *
     * @param multipartFiles
     * @return
     */
    public static List<InputStream> UnZip(MultipartFile multipartFiles){
        String originalFilename = multipartFiles.getOriginalFilename();
        //是否是zip文件类型
        if (!originalFilename.endsWith(".zip")){
            throw new RuntimeException(originalFilename+"文件格式错误！请上传.zip格式文件");
        }
        //解压
        List<InputStream> inputStreamList = new ArrayList<>();
        ZipInputStream zipInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        String zipEntryFile;
        try {
            zipInputStream = new ZipInputStream(multipartFiles.getInputStream());
            bufferedInputStream = new BufferedInputStream(zipInputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry())!=null){
                zipEntryFile = zipEntry.getName();
                //文件名称为空
                Assert.notNull(zipEntryFile,"压缩文件中子文件的名字格式不正确");
                //每个文件的流
                byte[] bytes = new byte[(int)zipEntry.getSize()];
                bufferedInputStream.read(bytes,0,(int)zipEntry.getSize());
                InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                inputStreamList.add(bufferedInputStream);
                byteArrayInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedInputStream!=null){
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipInputStream!=null){
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return inputStreamList;
    }

//    /**
//     *
//     * @param ins
//     * @param file
//     */
//    public static void inputStreamToFile(InputStream ins, File file) {
//        try {
//            OutputStream os = new FileOutputStream(file);
//            int bytesRead = 0;
//            byte[] buffer = new byte[8192];
//            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//            os.close();
//            ins.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BusinessException(e.getMessage());
//        }
//    }

    /**
     * 压缩文件夹或文件为zip
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {
        if (fileToZip.isHidden()) {
            return;
        }
        InputStream fis = null;
        try {
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }

            fis = Files.newInputStream(fileToZip.toPath());
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[10240];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

        } catch (FileNotFoundException e) {
            throw new BusinessException(e.getMessage());
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        } finally {
            try {
                if(fis!=null){
                    fis.close();
                }
                if(zipOut!=null){
                    zipOut.closeEntry();
                }
            } catch (IOException e) {
                throw new BusinessException(e.getMessage());
            }
        }

    }

}
