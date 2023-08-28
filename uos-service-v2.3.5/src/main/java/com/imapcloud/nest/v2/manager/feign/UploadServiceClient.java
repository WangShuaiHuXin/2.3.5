package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DynamicChunkInitInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DynamicChunkMetadataOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname UploadServiceClient
 * @Description 文件上传客户端接口
 * @Date 2023/2/16 15:25
 * @Author Carnival
 */

@FeignClient(contextId = "upload-service-client", name = "geoai-file-service",
        configuration = TokenRelayConfiguration.class)
public interface UploadServiceClient {

    @PostMapping(value ="files/upload" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileStorageOutDO> uploadFile(@RequestPart(value = "file") MultipartFile file, @SpringQueryMap CommonFileInDO params);

}
