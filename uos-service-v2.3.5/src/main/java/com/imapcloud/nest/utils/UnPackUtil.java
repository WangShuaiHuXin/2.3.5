package com.imapcloud.nest.utils;

import cn.hutool.core.io.FileUtil;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * 解压工具类
 * @deprecated 2.2.3
 * @author: zhengxd
 * @create: 2020/11/19
 **/
@Slf4j
@Component
@Deprecated
public class UnPackUtil {

//    @Autowired
//    private UploadManager uploadManager;
//
//    @Autowired
//    private FileManager fileManager;
//
//    private static UploadManager staticUploadManager;
//
//    private static FileManager staticFileManager;
//
//    @PostConstruct
//    public void init() {
//        staticUploadManager = uploadManager;
//        staticFileManager = fileManager;
//    }

//    /**
//     * zip文件解压
//     * @deprecated 2.2.3，将在后续版本删除
//     * @param filename 文件名称
//     * @param destPath 解压文件路径
//     * @param password 解压密码(如果有)
//     */
//    @Deprecated
//    public static void unPackZip(String filename, String destPath, String password,String unPackPath) {
//        try {
//            // 判断是否为zip包
//            if (DataConstant.ZIP.equalsIgnoreCase(filename.substring(filename.lastIndexOf(".") + 1))) {
//                Path tempFile = Files.createTempFile("zip_", ".zip");
//                try(InputStream inputSteam = staticFileManager.getInputSteam(destPath + SymbolConstants.SLASH_LEFT + filename)){
//                    FileCopyUtils.copy(inputSteam, Files.newOutputStream(tempFile));
//                }
////                File zipFile = new File(destPath + "/" + filename);
//                log.info(destPath + "/" + filename);
//                ZipFile zip = new ZipFile(tempFile.toFile());
//
//                /* 设置编码*/
////                String chartSet = codeString(zipFile.getPath());
//
//                // 设置压缩包编码
//                String chartSet = "UTF-8";
////                if (testEncoding(destPath + File.separator + filename, Charset.forName("UTF-8")) == false){
////                    chartSet = "GBK";
////                }
//                log.info("chartSet: " + chartSet);
//
//                zip.setFileNameCharset(chartSet);
//
//                log.info("开始解压zip包....");
//                log.info("地址:"+destPath+unPackPath);
//                zip.extractAll(destPath + unPackPath);
//                log.info("结束解压zip包....");
//                // 如果解压需要密码
//                if (zip.isEncrypted()) {
//                    zip.setPassword(password);
//                }
//            } else {
//                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPDATE.getContent()));
//            }
//
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()));
//        }
//    }

//    /**
//     * 获得文件编码
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    public static String codeString(String fileName) throws Exception {
//        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
//        int p = (bin.read() << 8) + bin.read();
//        bin.close();
//        String code;
//
//        switch (p) {
//            case 0xefbb:
//                code = "UTF-8";
//                break;
//            case 0xfffe:
//                code = "Unicode";
//                break;
//            case 0xfeff:
//                code = "UTF-16BE";
//                break;
//            default:
//                code = "GBK";
//        }
//
//        return code;
//    }
}
