package com.imapcloud.sdk.manager.media;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.media.entity.CpsUploadFileEntity;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.MediaFile;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多媒体系统 2代
 *
 * @author: zhengxd
 * @create: 2020/9/8
 **/
public class MediaManagerV2 {
    private final static String FUNCTION_TOPIC = Constant.MEDIA_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public MediaManagerV2(Client client) {
        this.client = client;
    }


    /**
     * 根据任务执行ID从CPS数据列表
     *
     * @param execId
     * @param handle
     */
    public void getMediaInfoList(String execId, UserHandle<List<MediaFile>> handle) {
        ProxyHandle<List<MediaFile>> ph = new ProxyHandle<List<MediaFile>>() {
            @Override
            public void success(List<MediaFile> mediaFiles, String msg) {
                handle.handle(mediaFiles, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        HashMap<String, Object> param = new HashMap<>(2);
        param.put("execMissionID", execId);
        ClientProxy.proxyPublishParamList(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C0, param, ph, MediaFile.class);
    }

    /**
     * 从无人机下载媒体文件到CPS(机巢)
     *
     * @param execId
     */
    public void downloadToNest(String execId, UserHandle<BaseResult3> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                handle.handle(baseResult3, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("execMissionID", execId);
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C1, param, ph);
    }


    /**
     * 从CPS上传媒体文件到服务器(有回调)
     *
     * @param cpsUploadFileEntity
     */
//    public void downloadToServer(CpsUploadFileEntity cpsUploadFileEntity, UserHandle<BaseResult3> handle) {
//        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
//            @Override
//            public void success(BaseResult3 baseResult3, String msg) {
//                handle.handle(baseResult3, true, msg);
//            }
//
//            @Override
//            public void error(boolean isError, String msg) {
//                if (isError) {
//                    handle.handle(null, false, msg);
//                }
//            }
//        };
//
//        HashMap<String, Object> param = new HashMap<>(4);
//        param.put("execMissionID", cpsUploadFileEntity.getExecId());
//        param.put("uploadMode", 0);
//        Map<String, Object> uploadParams = new HashMap<>(8);
//        uploadParams.put("url", cpsUploadFileEntity.getUrl());
//        uploadParams.put("chunkInitUrl", cpsUploadFileEntity.getChunkInitUrl());
//        uploadParams.put("chunkCombineUrl", cpsUploadFileEntity.getChunkCombineUrl());
//        uploadParams.put("chunkSyncUrl", cpsUploadFileEntity.getChunkSyncUrl());
//        uploadParams.put("uploadByChunks", cpsUploadFileEntity.getUploadByChunks());
//        param.put("uploadParams", uploadParams);
//        param.put("autoStartUp", cpsUploadFileEntity.getAutoStartUp());
//        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C2, param, ph);
//    }

    /**
     * 根据任务执行ID从机巢上传媒体文件缩略图到服务器(未支持，有无回调未知)
     *
     * @param execId
     * @param url
     */
    public void downloadThumbnailToServer(String execId, String url) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("execMissionID", execId);
        param.put("url", url);
        ClientProxy.proxyPublishNone(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C3, param);
    }


    /**
     * 根据媒体文件ID列表从机巢上传媒体文件到服务器（若媒体文件不存在机巢，会先自动从无人机下载媒体文件）
     *
     * @param cpsUploadFileEntity
     */
    public void downloadToServer(CpsUploadFileEntity cpsUploadFileEntity, UserHandle<BaseResult3> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                handle.handle(baseResult3, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        HashMap<String, Object> param = new HashMap<>(4);
        String code = Constant.MEDIA_MANAGER_2_C2;
        if (CollectionUtil.isNotEmpty(cpsUploadFileEntity.getFileIdList())) {
            code = Constant.MEDIA_MANAGER_2_C7;
            param.put("fileIdList", cpsUploadFileEntity.getFileIdList());
        }
        if (StringUtils.isNotEmpty(cpsUploadFileEntity.getExecId())) {
            param.put("execMissionID", cpsUploadFileEntity.getExecId());
        }
        param.put("uploadMode", 0);
        param.put("autoStartUp", cpsUploadFileEntity.getAutoStartUp());
        Map<String, Object> uploadParams = new HashMap<>(8);
        uploadParams.put("url", cpsUploadFileEntity.getUrl());
        uploadParams.put("chunkInitUrl", cpsUploadFileEntity.getChunkInitUrl());
        uploadParams.put("chunkCombineUrl", cpsUploadFileEntity.getChunkCombineUrl());
        uploadParams.put("chunkSyncUrl", cpsUploadFileEntity.getChunkSyncUrl());
        uploadParams.put("uploadByChunks", cpsUploadFileEntity.getUploadByChunks());
        // @since 2.2.3，新版文件上传参数
        uploadParams.put("v2SmallFileUploadUrl", cpsUploadFileEntity.getOssConfig().getUploadUrl());
        uploadParams.put("v2ChunkUpload", cpsUploadFileEntity.getUploadByChunks());
        uploadParams.put("v2ChunkInitUrl", cpsUploadFileEntity.getOssConfig().getChunkInitUrl());
        uploadParams.put("v2ChunkUploadUrl", cpsUploadFileEntity.getOssConfig().getChunkUploadUrl());
        uploadParams.put("v2ChunkSize", cpsUploadFileEntity.getOssConfig().getChunkSize().toBytes());
        uploadParams.put("v2ChunkSyncUrl", cpsUploadFileEntity.getChunkSyncUrl());
        uploadParams.put("v2ChunkCombineCallbackUrl", cpsUploadFileEntity.getOssConfig().getChunkComposeNotifyUrl());
        uploadParams.put("v2AuthUrl", cpsUploadFileEntity.getOssConfig().getAuthUrl());
        param.put("uploadParams", uploadParams);
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, code, param, ph, AirIndexEnum.getInstance(cpsUploadFileEntity.getUavWhich()));
    }

    /**
     * 根据媒体文件ID列表从飞机下载媒体文件到机巢
     *
     * @param fileIdList
     */
    public void downloadToNest(List<String> fileIdList, UserHandle<BaseResult3> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                handle.handle(baseResult3, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("FileIdList", fileIdList);
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C6, param, ph);
    }

    /**
     * 重置媒体管理器(暂停同步数据)
     *
     * @param handle
     */
    public void resetMediaManager(UserHandle<BaseResult3> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                handle.handle(baseResult3, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C11, null, ph);
    }

    /**
     * 根据媒体文件ID列表从机巢上传媒体文件缩略图到服务器(为支持，废弃，应有回调)
     *
     * @param fileIdList
     * @param url
     */
    public void downloadThumbnailToServer(List<String> fileIdList, String url) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("FileIdList", fileIdList);
        param.put("url", url);
        ClientProxy.proxyPublishNone(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_2_C8, param);
    }


    private void getResultInt(String code, UserHandle<Integer> handle, Map<String, Object> param, String key) {
        ProxyHandle<Integer> ph = new ProxyHandle<Integer>() {
            @Override
            public void success(Integer res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key, Integer.class);
    }

    private void getResultBoolean(String code, UserHandle<Boolean> handle, Map<String, Object> param) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean aBoolean, String msg) {
                handle.handle(aBoolean, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, code, param, ph);
    }


}
