package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.feign.UploadServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @Classname UploadManager
 * @Description 文件上传管理
 * @Date 2023/2/16 15:45
 * @Author Carnival
 */
@Slf4j
@Component
public class UploadManager {

    @Resource
    private UploadServiceClient uploadServiceClient;

    /**
     * 小文件上传
     */
    public Optional<FileStorageOutDO> uploadFile(CommonFileInDO body) {
        InputStream inputStream = body.getInputStream();
        String fileName = body.getFileName();
        if (!ObjectUtils.isEmpty(inputStream) && StringUtils.hasText(fileName)) {
            try (InputStream is = inputStream) {
                MultipartFile file = new MockMultipartFile(fileName, fileName, null, is);
                body.setInputStream(null);
                Result<FileStorageOutDO> result = uploadServiceClient.uploadFile(file, body);
                if (result.isOk()) {
                    return Optional.ofNullable(result.getData());
                } else {
                    log.error("upload file error code:{}, error msg:{}", result.getCode(), result.getMsg());
                }
            } catch (Exception e) {
                log.error("upload file exception", e);
            }
        }
        return Optional.empty();
    }
}
