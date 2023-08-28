package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.FileDownloadRecordEntity;
import com.imapcloud.nest.v2.dao.mapper.FileDownloadRecordMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.FileDownloadRecordInDO;
import com.imapcloud.nest.v2.manager.sql.FileDownloadRecordManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 文件下载记录
 *
 * @author boluo
 * @date 2023-05-09
 */
@Component
public class FileDownloadRecordManagerImpl implements FileDownloadRecordManager {

    @Resource
    private FileDownloadRecordMapper fileDownloadRecordMapper;

    @Override
    public int save(FileDownloadRecordInDO fileDownloadRecordInDO) {

        FileDownloadRecordEntity fileDownloadRecordEntity = new FileDownloadRecordEntity();
        fileDownloadRecordEntity.setFileDownloadRecordId(fileDownloadRecordInDO.getFileDownloadRecordId());
        fileDownloadRecordEntity.setAnnotationKey(fileDownloadRecordInDO.getAnnotationKey());
        fileDownloadRecordEntity.setParam(fileDownloadRecordInDO.getParam());
        fileDownloadRecordEntity.setDownloadStatus(fileDownloadRecordInDO.getDownloadStatus());
        fileDownloadRecordInDO.setInsertAccount(fileDownloadRecordEntity);

        return fileDownloadRecordMapper.insert(fileDownloadRecordEntity);
    }

    @Override
    public int updateDownloadStatus(String fileDownloadRecordId) {

        if (fileDownloadRecordId == null) {
            return 0;
        }
        LambdaUpdateWrapper<FileDownloadRecordEntity> updateWrapper = Wrappers.lambdaUpdate(FileDownloadRecordEntity.class)
                .eq(FileDownloadRecordEntity::getFileDownloadRecordId, fileDownloadRecordId)
                .set(FileDownloadRecordEntity::getDownloadStatus, 1);
        return fileDownloadRecordMapper.update(null, updateWrapper);
    }
}
