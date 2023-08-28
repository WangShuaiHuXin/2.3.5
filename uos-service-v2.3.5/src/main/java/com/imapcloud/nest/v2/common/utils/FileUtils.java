package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.constant.SymbolConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Vastfy
 * @date 2022/07/13 18:28
 * @since 1.9.7
 */
@Slf4j
public abstract class FileUtils extends org.apache.commons.io.FileUtils {

    private static final String JAVA_TMP_IO_DIR = System.getProperty("java.io.tmpdir");

    private FileUtils(){}

    public static String getFilename(String filename, boolean returnSuffix){
        String fn = StringUtils.getFilename(filename);
        if(returnSuffix){
            return fn;
        }
        int pointIndex = fn.lastIndexOf(SymbolConstants.POINT);
        if(pointIndex != -1){
            return fn.substring(0, pointIndex);
        }
        return fn;
    }

    public static void deleteFinally(File... files) {
        if(Objects.nonNull(files) && files.length > 0){
            deleteFinally(Arrays.stream(files).collect(Collectors.toList()));
        }
    }

    public static void deleteFinally(Collection<File> files) {
        if(!CollectionUtils.isEmpty(files)){
            try {
                for (File file : files) {
                    deleteFileOrDir(file);
                }
            }catch (Exception e){
                log.error("删除文件失败", e);
            }
        }
    }

    private static void deleteFileOrDir(File file) {
        if (Objects.nonNull(file) && file.exists()) {
            if(file.isFile()){
                deleteFileOrEmptyDir(file);
                return;
            }
            if(file.isDirectory()){
                deleteDir(file);
            }
        }
    }

    private static void deleteDir(File dir){
        deleteFinally(dir.listFiles());
        clearEmptyDirectory(dir);
    }

    private static void clearEmptyDirectory(File dir){
        File[] subFiles = dir.listFiles();
        if(Objects.isNull(subFiles) || subFiles.length < 1){
            deleteFileOrEmptyDir(dir);
        }
    }

    private static void deleteFileOrEmptyDir(File file){
        boolean deleted = file.delete();
        if (!deleted) {
            file.deleteOnExit();
        }
    }

//    /**
//     * 删除文件
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    public static void deleteFile(List<String> urls, boolean isMinIO){
//        if(CollectionUtil.isNotEmpty(urls)){
//            if(isMinIO){
//                urls = urls.stream()
//                        .filter(x->!StringUtils.isEmpty(x))
//                        .collect(Collectors.toList());
//                MinIoUnit.rmObjects(urls);
//            }else{
//                for(String url : urls){
//                    File file = Paths.get(url).toFile();
//                    if(FileUtil.isEmpty(file)){
//                        FileUtil.del(file);
//                    }
//                }
//            }
//        }
//    }
}
