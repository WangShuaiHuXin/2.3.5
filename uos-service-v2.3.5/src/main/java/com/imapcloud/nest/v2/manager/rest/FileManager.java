package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.FileAccessLinkOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileReplicaOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SessionSecurityTokenOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ThumbnailStorageOutDO;
import com.imapcloud.nest.v2.manager.feign.FileServiceClient;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Classname FileManager
 * @Description 文件服务管理
 * @Date 2023/2/16 11:32
 * @Author Carnival
 */
@Slf4j
@Component
public class FileManager {

    @Resource
    private FileServiceClient fileServiceClient;

    private static final String REQUEST_METHOD_GET = "GET";

    private static final Integer CONNECT_TIMEOUT = 20 * 1000;


    /**
     * 检查文件是否存在
     */
    public Boolean checkFileExists(String filePath) {
        Result<Boolean> result = fileServiceClient.checkFileExists(filePath);
        if (result.isOk()) {
            return result.getData();
        } else {
            log.error("check file exists code:{}, error msg:{}", result.getCode(), result.getMsg());
        }
        return false;
    }

    /**
     * 获取文件下载链接
     */
    public String getDownloadLink(String filePath) {
        return getDownloadLink(filePath, null);
    }

    /**
     * 获取文件下载链接
     * @param filePath 文件存储路径
     * @param intranet 是否内网
     */
    public String getDownloadLink(String filePath, Boolean intranet) {
        FileLinkInDO params = new FileLinkInDO();
        params.setFilePath(filePath);
        params.setOnlyIntranet(intranet);
        Result<FileAccessLinkOutDO> result = fileServiceClient.getDownloadLink(params);
        if (result.isOk()) {
            FileAccessLinkOutDO data = result.getData();
            return data.getAccessUrl();
        } else {
            log.error("get download link error code:{}, error msg:{}", result.getCode(), result.getMsg());
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 批量删除图片
     */
    public void deleteFiles(List<String> filePaths) {
        if(!CollectionUtils.isEmpty(filePaths)){
            int batchSize = 64;
            List<String> batch = new ArrayList<>(batchSize);
            for (String filePath : filePaths) {
                batch.add(filePath);
                if(batch.size() >= batchSize){
                    fileServiceClient.deleteFiles(batch);
                    batch.clear();
                }
            }
            if(!CollectionUtils.isEmpty(batch)){
                fileServiceClient.deleteFiles(batch);
                batch.clear();
            }
        }
    }

    /**
     * 图片生成缩略图
     */
    public String generateThumbnail(String srcImagePath, Double scale, boolean async) {
        ThumbnailRuleInDO body = new ThumbnailRuleInDO();
        body.setSrcImagePath(srcImagePath);
        body.setScaleWidth(scale);
        // 默认全为异步
        body.setAsync(true);
        try {
            Result<ThumbnailStorageOutDO> result = fileServiceClient.generateThumbnail(body);
            if (result.isOk()) {
                return Optional.ofNullable(result.getData())
                        .map(r -> r.getStoragePath() + SymbolConstants.SLASH_LEFT + r.getFilename())
                        .orElse(srcImagePath);
            } else {
                log.error("generate thumbnail code:{}, error msg:{}", result.getCode(), result.getMsg());
            }
        }catch (Exception e){
            log.error("generate thumbnail error", e);
        }
        return srcImagePath;
    }

    /**
     * 动态链接获取文件流
     * 大文件获取，请勿使用该方法，可能堆内存溢出，可以使用方法{@link FileManager#handleInputSteam(java.lang.String, java.util.function.Consumer)}替换
     */
    public InputStream getInputSteam(String filePath) {
        String strUrl = this.getDownloadLink(filePath);
        if (StringUtils.hasText(strUrl)) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(strUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(REQUEST_METHOD_GET);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                final ByteArrayOutputStream output = new ByteArrayOutputStream();
                IOUtils.copy(conn.getInputStream(), output);
                return new ByteArrayInputStream(output.toByteArray());
            } catch (Exception e) {
                log.error("get inputSteam error :{}", e.toString());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        return null;
    }

    /**
     * 动态链接获取文件流，并对文件流进行处理
     */
    public void handleInputSteam(String filePath, Consumer<InputStream> consumer) {
        String strUrl = this.getDownloadLink(filePath);
        if (!StringUtils.hasText(strUrl)) {
            log.warn("获取文件[{}]远程访问链接为空", filePath);
            return;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD_GET);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            InputStream inputStream = conn.getInputStream();
            if(Objects.isNull(inputStream)){
                log.warn("远程获取文件流为空 ==> {}", filePath);
                return;
            }
            consumer.accept(inputStream);
        } catch (Exception e) {
            log.error("get inputSteam error :{}", e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 动态链接获取临时文件
     */
    public File createTempFile(String filePath) {
        InputStream inputStream = this.getInputSteam(filePath);
        if (!ObjectUtils.isEmpty(inputStream)) {
            try (InputStream is = inputStream) {
                String suffix = filePath.substring(filePath.lastIndexOf("."));
                File tempFile = File.createTempFile(suffix, suffix);
                FileUtils.copyInputStreamToFile(is, tempFile);
                return tempFile;
            } catch (Exception e) {
                log.error("create temp file error:{}", e.toString());
            }
        }
        return null;
    }

    /**
     * 文件下载到本地
     *
     * @param filePath 文件路径
     * @return {@link String} 本地文件路径
     */
    public String downloadLocal(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = this.getInputSteam(filePath);
            if (inputStream == null) {
                return null;
            }
            // 本地文件地址
            String localFilePath;
            if (filePath.startsWith("/")) {
                localFilePath = String.format("/downloadLocal%s", filePath);
            } else {
                localFilePath = String.format("/downloadLocal/%s", filePath);
            }
            log.info("#FileManager.downloadLocal# filePath={}, localFilePath={}", filePath, localFilePath);
            File localFile = new File(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            localFile.createNewFile();
            FileUtils.copyInputStreamToFile(inputStream, localFile);
            return localFilePath;
        } catch (Exception e) {
            log.error("#FileManager.downloadLocal# filePath={}", filePath, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("#FileManager.downloadLocal# close error filePath={}", filePath, e);
            }
        }
        return null;
    }

    public void deleteLocalFile(String localFilePath) {

        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            localFile.delete();
        }
    }

    /**
     * 复制文件，返回副本的存储路径，注意：该接口为默认同步复制，如果文件较大，请使用异步方式(传回调地址)
     * @param filePath  带复制的文件
     * @return  副本文件信息
     */
    public String copyFile(String filePath, String callbackUrl){
        FileCopyInDO fileCopyInDO = new FileCopyInDO();
        fileCopyInDO.setFilePath(filePath);
        fileCopyInDO.setAsync(false);
        if(StringUtils.hasText(callbackUrl)){
            fileCopyInDO.setAsync(true);
            fileCopyInDO.setCallbackUrl(callbackUrl);
        }
        Result<FileReplicaOutDO> result = fileServiceClient.copyFile(fileCopyInDO);
        if(result.isOk()){
            return Optional.ofNullable(result.getData())
                    .map(FileReplicaOutDO::getReplicaPath)
                    .orElse(filePath);
        }else{
            log.error("复制文件失败 ==> {}", result);
            return filePath;
        }
    }

    /**
     * 设置文件标签
     * @param filePath  文件路径
     * @param tagMap    标签列表
     */
    public void setFileTags(String filePath, Map<String, String> tagMap){
        FileTagSettingInDO fileTagSettingInDO = new FileTagSettingInDO();
        fileTagSettingInDO.setFilePath(filePath);
        fileTagSettingInDO.setFileTags(getFileTag(tagMap));
        Result<List<FileTagSettingInDO.FileTag>> result = fileServiceClient.setFileTags(fileTagSettingInDO);
        if(result.isOk()){
            return;
        }
        log.error("设置文件标签失败 ==> {}", result);
    }

    private List<FileTagSettingInDO.FileTag> getFileTag(Map<String, String> tagMap) {
        if(!CollectionUtils.isEmpty(tagMap)){
            return tagMap.entrySet()
                    .stream()
                    .map(r -> {
                        FileTagSettingInDO.FileTag tag = new FileTagSettingInDO.FileTag();
                        tag.setKey(r.getKey());
                        tag.setValue(r.getValue());
                        return tag;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 获取临时token
     * @param expiry
     * @param outEndPoint
     * @return
     */
    public SessionSecurityTokenOutDO getSecurityToken(Integer expiry , Boolean outEndPoint) {
        SessionSecurityTokenInDO inDO = new SessionSecurityTokenInDO();
        inDO.setExpiry(expiry);
        inDO.setOuterEndpoint(outEndPoint);
        Result<SessionSecurityTokenOutDO> result = fileServiceClient.getSecurityToken(inDO);
        if (result.isOk()) {
            return result.getData();
        } else {
            log.error("getSecurityToken:{}, error msg:{}", result.getCode(), result.getMsg());
        }
        return new SessionSecurityTokenOutDO();
    }

}
