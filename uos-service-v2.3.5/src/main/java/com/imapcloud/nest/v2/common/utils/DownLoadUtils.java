package com.imapcloud.nest.v2.common.utils;

import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DownLoadUtils.java
 * @Description DownLoadUtils
 * @createTime 2022年07月18日 15:31:00
 */
@Component
@Slf4j
public class DownLoadUtils {

    @Resource
    private FileManager fileManager;

    private static FileManager staticFileManager;

    @PostConstruct
    public void init() {
        staticFileManager = fileManager;
    }

    public static int FILE_SIZE = 1024;

    public static String SUFFIX_FILE = ".zip";

    /**
     * 批量下载、压缩
     * @param paths
     * @param minIO
     * @param response
     */
    public static void downLoadCommons(List<String> paths , boolean minIO,HttpServletResponse response){
        for(String path : paths){
            downLoadCommon(path,minIO,response);
        }
    }

    /**
     * 下载压缩单个文件
     * @param path
     * @param minIO
     * @param response
     */
    public static void downLoadCommon(String path , boolean minIO , HttpServletResponse response){
        try(InputStream fileInputStream = staticFileManager.getInputSteam(path);
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
            String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/")+1), "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] bytes = new byte[1024*10];
            int len = 0;
            ZipEntry zipEntry = new ZipEntry(String.format("%s.zip",fileName));
            zipOutputStream.putNextEntry(zipEntry);
            long startTime = System.currentTimeMillis();
            while ((len = fileInputStream.read(bytes)) != -1) {
                log.info("ing:{}",len);
                zipOutputStream.write(bytes, 0, len);
            }
            log.info("耗时【downLoadCommon】:{}",(System.currentTimeMillis()-startTime)/1000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public static void downLoadCommon(String path , boolean minIO , HttpServletResponse response, String characterEncoding){
//        try(InputStream fileInputStream = minIO? MinIoUnit.getObject(path) :new FileInputStream(path);
//            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())){
//            String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/")+1), characterEncoding);
//            response.setCharacterEncoding(characterEncoding);
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
//            byte[] bytes = new byte[1024*10];
//            int len = 0;
//            ZipEntry zipEntry = new ZipEntry(String.format("%s.zip",fileName));
//            zipOutputStream.putNextEntry(zipEntry);
//            long startTime = System.currentTimeMillis();
//            while ((len = fileInputStream.read(bytes)) != -1) {
//                log.info("ing:{}",len);
//                zipOutputStream.write(bytes, 0, len);
//            }
//            log.info("耗时【downLoadCommon】:{}",(System.currentTimeMillis()-startTime)/1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 下载并压缩，高效
     * @param paths
     * @param isMinIO
     * @param response
     */
    public static void downloadZipWithPip(List<String> paths ,String zipName, HttpServletResponse response) {
        String fileName = String.format("%s-%s-%s", StringUtils.isEmpty(zipName)?"file":zipName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),SUFFIX_FILE);

        try(WritableByteChannel out = Channels.newChannel(response.getOutputStream())) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);
            response.setHeader("filename", fileName);
            Pipe pipe = Pipe.open();
            //异步任务
            CompletableFuture.runAsync(() -> runWithAsync(pipe, paths));

            //获取读通道
            ReadableByteChannel readableByteChannel = pipe.source();
            ByteBuffer buffer = ByteBuffer.allocate(((int) FILE_SIZE)*10);
            while (readableByteChannel.read(buffer)>= 0) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("下载失败",e);
        }

    }

    /**
     * 异步通道
     * @param pipe
     */
    private static void runWithAsync(Pipe pipe, List<String> paths) {

        try(ZipOutputStream zipOutputStream = new ZipOutputStream(Channels.newOutputStream(pipe.sink()));
            WritableByteChannel out = Channels.newChannel(zipOutputStream)) {
            for (int i =0; i<paths.size();i++) {
                String path = paths.get(i);
                zipOutputStream.putNextEntry(new ZipEntry(path.substring(path.lastIndexOf("/")+1)));
                try(InputStream inputSteam = staticFileManager.getInputSteam(paths.get(i))){
                    if(Objects.nonNull(inputSteam)){
                        try(ReadableByteChannel readableByteChannel = Channels.newChannel(inputSteam)){
                            ByteBuffer buffer=ByteBuffer.allocate(10 * FILE_SIZE);
                            while(readableByteChannel.read(buffer) != -1){
                                buffer.flip();
                                while(buffer.hasRemaining()){
                                    out.write(buffer);
                                }
                                buffer.clear();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }
}
