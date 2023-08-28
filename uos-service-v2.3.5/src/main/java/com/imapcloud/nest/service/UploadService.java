package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.UploadEntity;
import com.imapcloud.nest.utils.RestRes;

/**
 * @author hc
 * @date 2021/11/04
 */
public interface UploadService extends IService<UploadEntity> {

//    /**
//     * 分片上传初始化
//     * @deprecated 2.2.3，新版文件上传初始化接口已迁移至文件服务，将在后续版本删除该接口
//     */
//    @Deprecated
//    RestRes initMultiPartUpload(UploadEntity uploadEntity);

//    /**
//     * 完成分片上传
//     * @deprecated 2.2.3，新版文件上传无需手动合并，将在后续版本删除该接口
//     */
//    @Deprecated
//    RestRes mergeMultipartUpload(UploadEntity uploadEntity);

    /**
     * 分片上传初始化-Cps
     * @deprecated 2.2.3，新版文件上传初始化接口已迁移至文件服务，将在后续版本删除该接口
     */
    @Deprecated
    RestRes initMultiPartUploadCps(UploadEntity uploadEntity);

    /**
     * 完成分片上传-Cps
     * @deprecated 2.2.3，新版文件上传无需手动合并，将在后续版本删除该接口
     */
    @Deprecated
    RestRes mergeMultipartUploadCps(UploadEntity uploadEntity);

    /**
     * 删除upload
     *
     * @param filePath 文件路径
     */
    void deleteByFilePath(String filePath);
}
