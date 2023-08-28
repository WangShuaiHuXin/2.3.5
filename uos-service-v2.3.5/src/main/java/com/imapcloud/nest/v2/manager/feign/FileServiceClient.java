package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.FileAccessLinkOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileReplicaOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SessionSecurityTokenOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ThumbnailStorageOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname FileServiceClient
 * @Description 文件服务客户端接口
 * @Date 2023/2/16 11:30
 * @Author Carnival
 */
@RequestMapping("files")
@FeignClient(contextId = "file-service-client", name = "geoai-file-service",
        configuration = TokenRelayConfiguration.class)
public interface FileServiceClient {

    @GetMapping("exists")
    Result<Boolean> checkFileExists(@RequestParam String filePath);

    @GetMapping("link")
    Result<FileAccessLinkOutDO> getDownloadLink(@SpringQueryMap FileLinkInDO params);

    @DeleteMapping
    Result<Void> deleteFiles(@RequestBody List<String> filePaths);

    @PostMapping("thumbnail")
    Result<ThumbnailStorageOutDO> generateThumbnail(@RequestBody ThumbnailRuleInDO body);

    @PostMapping("copy")
    Result<FileReplicaOutDO> copyFile(@RequestBody FileCopyInDO body);

    @PostMapping("tag")
    Result<List<FileTagSettingInDO.FileTag>> setFileTags(@RequestBody FileTagSettingInDO body);

    @GetMapping("security/token")
    Result<SessionSecurityTokenOutDO> getSecurityToken(@SpringQueryMap SessionSecurityTokenInDO body);

}
