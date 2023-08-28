package com.imapcloud.nest.utils.nestLogsUtil;

import cn.hutool.core.io.FileUtil;
import com.imapcloud.nest.enums.NestLogTypeEnum;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 基站日志包解析工具类
 *
 * @author wmin
 */
@Slf4j
public class NestLogsParseUtil {

    public static class NestLogZip {
        private String originalFilename;
        private InputStream inputStream;

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

    }

    private static final GeoaiUosProperties geoaiUosProperties = SpringContextUtils.getBean(GeoaiUosProperties.class);

    /**
     * @deprecated 2.2.3，将在2023/06/30后删除
     */
    @Deprecated
    public static String uploadZip2minio(String nestUuid, NestLogZip nestLogZip) {
        String originalFilename = nestLogZip.getOriginalFilename();
        String filename = System.currentTimeMillis() + originalFilename;
        String uploadFolder = geoaiUosProperties.getNestLog().getStorePath() + nestUuid + "/" + LocalDate.now() + "/";
        //调用minio上传文件
        String shotFilePath = MinIoUnit.uploadZip(geoaiUosProperties.getMinio().getBucketName(), nestLogZip.getInputStream(), filename, uploadFolder);
        return geoaiUosProperties.getStore().getOriginPath() + shotFilePath;
    }

    public static NestLogZip nestLogZip(MultipartFile file) {
        if (Objects.nonNull(file)) {
            try {
                NestLogZip nestLogZip = new NestLogZip();
                nestLogZip.setInputStream(file.getInputStream());
                nestLogZip.setOriginalFilename(file.getOriginalFilename());
                return nestLogZip;
            } catch (IOException e) {

            }
        }
        return null;
    }


    public static boolean delLogZip(String saveUrl) {
        return FileUtil.del(saveUrl);
    }

    @Data
    @Accessors(chain = true)
    public static class UploadRes {
        private String zipPath;
        private String unzipPath;
        private String filename;
        private String originalFilename;
    }

    @Data
    @Accessors(chain = true)
    public static class ParseRes {
        private NestLogTypeEnum nestLogTypeEnum;
        private String url;
        private String filename;
    }
}
